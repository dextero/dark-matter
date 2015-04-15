BIN_DIR = bin
OBJ_DIR = obj

OUTPUT = $(BIN_DIR)/test

CXXFLAGS = -std=c++11 -Wall -Wextra -g
LDFLAGS =
LDLIBS = -lskylens -lshapelens -ltmv -lsqlite3 -lgsl -lblas -lcfitsio

SOURCES = $(shell find ./ -name '*.cpp')
OBJECTS = $(addprefix $(OBJ_DIR)/,$(SOURCES:.cpp=.o))
_ = $(shell echo "$(OBJECTS)" | sort | uniq | xargs dirname | xargs mkdir -p)

default: prepare $(OUTPUT)

prepare:
	@mkdir -p "$(BIN_DIR)"

print_compile_cmd:
	@echo "$(CXX) $(CXXFLAGS) -c -o OUTPUT INPUT"

print_link_cmd:
	@echo "$(CXX) $(LDFLAGS) -o OUTPUT INPUT $(LDLIBS)"

$(OUTPUT): print_compile_cmd $(OBJECTS) print_link_cmd
	@echo "LD $@"
	@$(CXX) $(LDFLAGS) -o $(OUTPUT) $(OBJECTS) $(LDLIBS)

obj/%.o: %.cpp
	@echo "CXX $@"
	@$(CXX) $(CXXFLAGS) -c -o $@ $^

clean:
	rm -rf "$(OBJ_DIR)"

.PHONY: prepare clean print_compile_cmd print_link_cmd
