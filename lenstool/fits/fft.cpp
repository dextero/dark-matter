#include <gsl/gsl_matrix.h>
#include <gsl/gsl_fft_complex_float.h>

#include <boost/lexical_cast.hpp>
#include <boost/format.hpp>

#include <iostream>
#include <fstream>
#include <exception>
#include <memory>

using boost::lexical_cast;
using std::operator ""s;

class critical_error: public std::exception
{
public:
    critical_error(const std::string& what):
        std::exception(),
        msg(what)
    {}

    virtual const char *what() const noexcept {
        return msg.c_str();
    }

private:
    std::string msg;
};

namespace {

void fft(std::vector<gsl_complex_float>& data,
         size_t n,
         size_t offset,
         size_t stride) {
    gsl_complex_packed_array_float ptr =
            (gsl_complex_packed_array_float)&data[offset];

    if (gsl_fft_complex_float_radix2_forward(ptr, stride, n) != GSL_SUCCESS) {
        throw critical_error("FFT failed");
    }
}

void ifft(std::vector<gsl_complex_float>& data,
          size_t n,
          size_t offset,
          size_t stride) {
    gsl_complex_packed_array_float ptr =
            (gsl_complex_packed_array_float)&data[offset];

    if (gsl_fft_complex_float_radix2_inverse(ptr, stride, n) != GSL_SUCCESS) {
        throw critical_error("FFT failed");
    }
}

void fft2(std::vector<gsl_complex_float>& data,
          size_t width,
          size_t height) {
    for (size_t i = 0; i < width; ++i) {
        fft(data, height, i, width);
    }

    for (size_t i = 0; i < height; ++i) {
        fft(data, width, i * width, 1);
    }
}

void ifft2(std::vector<gsl_complex_float>& data,
           size_t width,
           size_t height) {
    for (size_t i = 0; i < width; ++i) {
        ifft(data, height, i, width);
    }

    for (size_t i = 0; i < height; ++i) {
        ifft(data, width, i * width, 1);
    }
}

void do_fft2(std::istream& in,
             size_t in_width,
             size_t in_height,
             std::ostream& out)
{
    fprintf(stderr, "reading %zu x %zu image\n", in_width, in_height);
    std::vector<gsl_complex_float> buffer(in_width * in_height);
    for (size_t i = 0; i < in_width * in_height; ++i) {
        in.read((char*)&GSL_REAL(buffer[i]),
                sizeof(GSL_REAL(buffer[i])));
    }

    fprintf(stderr, "performing FFT\n");
    fft2(buffer, in_width, in_height);

    fprintf(stderr, "generating output\n");
    out.write((const char*)&buffer[0],
              buffer.size() * sizeof(buffer[0]));
}

void do_ifft2(std::istream& in,
              size_t in_width,
              size_t in_height,
              std::ostream& out)
{
    fprintf(stderr, "reading %zu x %zu image\n", in_width, in_height);
    std::vector<gsl_complex_float> buffer(in_width * in_height);
    in.read((char*)&buffer[0],
            buffer.size() * sizeof(buffer[0]));

    fprintf(stderr, "performing inverse FFT\n");
    ifft2(buffer, in_width, in_height);

    fprintf(stderr, "generating output\n");
    for (size_t i = 0; i < buffer.size(); ++i) {
        out.write((const char*)&GSL_REAL(buffer[i]),
                  sizeof(GSL_REAL(buffer[i])));
    }
}

} // namespace

int main(int argc, char** argv)
try {
    if (argc < 3) {
        throw critical_error((boost::format("usage: %1% WIDTH HEIGHT [ INPUT_FILE ]") % argv[0]).str());
    }

    bool inverse = false;
    if (argv[1] == "-i"s) {
        std::swap(argv[0], argv[1]);
        ++argv;
        --argc;
        inverse = true;
    }

    std::unique_ptr<std::ifstream> in(new std::ifstream());
    std::istream* in_ptr = &std::cin;
    if (argc > 3) {
        in->open(argv[3]);
        if (!in->is_open()) {
            throw critical_error("no such file or directory: "s + argv[3]);
        }
        in_ptr = in.get();
    }

    (inverse ? do_ifft2 : do_fft2)(*in_ptr,
                                   lexical_cast<size_t>(argv[1]),
                                   lexical_cast<size_t>(argv[2]),
                                   std::cout);

    return 0;
} catch (std::exception& e) {
    std::cerr << e.what() << std::endl;
    return -1;
}
