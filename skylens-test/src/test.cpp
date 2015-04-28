#include <skylens/SkyLens.h>
#include <skylens/Layer.h>
#include <skylens/Telescope.h>

#include <boost/make_shared.hpp>

#define TELESCOPE_CONFIG "data/telescope.conf"
#define TELESCOPE_FILTER "data/johnsonI.fits"

#define EXPOSURE_TIME_S 2000

#define LENS_ANGLE_FILE "data/test.fits"

#define COSMOLOGY_FILE "data/cosmology.conf"

#define MOFFAT_BETA 1.0
#define MOFFAT_FWHM 1.0
#define MOFFAT_FLUX 1.0
#define MOFFAT_ELLIPTICITY 1.0

#define SERSIC_INDEX 2.5
#define SERSIC_EFFECTIVE_RADIUS 5.55
#define SERSIC_MAG 0.5

using namespace skylens;

template<typename T>
void fits_try_read(fitsfile* fptr,
                   std::vector<std::string> options,
                   T& output,
                   bool dont_throw = false)
{
    for (size_t i = 0; i < options.size(); ++i) {
        try {
            shapelens::FITS::readKeyword(fptr, options[i], output);
        } catch (std::exception&) {
            if (i + 1 == options.size() && !dont_throw) {
                throw;
            }
        }
    }
}

LensingLayer::Parameters load(const std::string& fits_filename)
{
    struct fitsfile_guard {
        fitsfile_guard(const std::string& filename):
            fptr(shapelens::FITS::openFile(filename))
        {}

        ~fitsfile_guard() {
            shapelens::FITS::closeFile(fptr);
        }

        operator fitsfile*() { return fptr; }

    private:
        fitsfile* fptr;
    } fptr(fits_filename);

    LensingLayer::Parameters params;

    fits_try_read(fptr, { "SIDEL", "NAXIS1" },   params.sidelength);
    fits_try_read(fptr, { "OMEGA", "OMEGA_M" },  params.omega);
    fits_try_read(fptr, { "LAMBDA", "OMEGA_L" }, params.lambda);
    fits_try_read(fptr, { "ZLENS", "Z" },        params.z_lens);
    fits_try_read(fptr, { "H" },                 params.h);
    fits_try_read(fptr, { "LENSRESC" },          params.rescale_lens, true);

    params.z_source = 1.0; // TODO

    shapelens::Image<float> img;
    shapelens::FITS::readImage(fptr, img);

    new (&params.deflection_realpart) shapelens::Image<float>(params.sidelength, params.sidelength);
    new (&params.deflection_imagpart) shapelens::Image<float>(params.sidelength, params.sidelength);

    for (int i = 0; i < img.size() / 2; ++i) {
        params.deflection_realpart(i) = img(i * 2);
        params.deflection_imagpart(i) = img(i * 2 + 1);
    }

    return params;
}

int main(int argc, char** argv)
{
    static const size_t NUM_LENSES = [argc, argv]() {
        return argc <= 1 ? 1 : boost::lexical_cast<size_t>(argv[1]);
    }();

    std::cerr << NUM_LENSES << " lenses\n";

    std::cerr << "initializing telescope\n";
    Telescope telescope(TELESCOPE_CONFIG, TELESCOPE_FILTER);
    std::cerr << "initializing observation\n";
    Observation observation(telescope, EXPOSURE_TIME_S);
    observation.computeTransmittance(0.0, 0.0);
    observation.createSkyFluxLayer(1.0);

    std::cerr << "loading params\n";
    LensingLayer::Parameters params = load(LENS_ANGLE_FILE);
    params.z_source = 1.0 + NUM_LENSES;

    std::cerr << "initializing source model\n";
    SourceModelList sources;
#if 0
    sources.emplace_back(boost::make_shared<MoffatModel>(
            MOFFAT_BETA, MOFFAT_FWHM, MOFFAT_FLUX, MOFFAT_ELLIPTICITY));
#else
    const gsl_rng* r = Singleton<RNG>::getInstance().getRNG();
    complex<double> eps(1,0);
    complex<double> I(0,1);
    while (abs(eps) >= 0.99) { // restrict to viable ellipticities
        const float sigma_e = 0.3;
        eps = gsl_ran_rayleigh(r,sigma_e/M_SQRT2);
        eps *= exp(2.*I*M_PI*gsl_rng_uniform(r));
    }
    float flux = Conversion::mag2flux(SERSIC_MAG);
    const Filter& transmittance = observation.getTotalTransmittance();
    flux = Conversion::flux2photons(flux, EXPOSURE_TIME_S, telescope, transmittance);
    flux = Conversion::photons2ADU(flux, telescope.gain);
    shapelens::ShiftTransformation positions[] = {
        {{ 25,   0 }},
        {{ -5,  15 }},
        {{ -5, -15 }}
    };
    for (const auto& pos: positions) {
        sources.emplace_back(boost::make_shared<SersicModel>(
                SERSIC_INDEX, SERSIC_EFFECTIVE_RADIUS, flux, eps, 0, &pos));
    }
#endif
    std::cerr << sources.size() << "\n";
    new GalaxyLayer(params.z_source, sources);

    std::cerr << "initializing lenses\n";
    for (size_t i = 0; i < NUM_LENSES; ++i) {
        double redshift = (double)(i + 1) / (NUM_LENSES + 1);
        // that's NOT a memleak. LayerStack singleton takes ownership of all
        // created layers, which *must* be created dynamically.
        params.z_lens = redshift;
        new LensingLayer(params);
    }

    Image<float> output_img;
    std::cerr << "generating image\n";
    observation.makeImage(output_img);
    std::cerr << "saving image\n";
    output_img.save("output.fits");

    return 0;
}
