# CMAKE generated file: DO NOT EDIT!
# Generated by "Unix Makefiles" Generator, CMake Version 3.16

# Delete rule output on recipe failure.
.DELETE_ON_ERROR:


#=============================================================================
# Special targets provided by cmake.

# Disable implicit rules so canonical targets will work.
.SUFFIXES:


# Remove some rules from gmake that .SUFFIXES does not remove.
SUFFIXES =

.SUFFIXES: .hpux_make_needs_suffix_list


# Suppress display of executed commands.
$(VERBOSE).SILENT:


# A target that is always out of date.
cmake_force:

.PHONY : cmake_force

#=============================================================================
# Set environment variables for the build.

# The shell in which to execute make rules.
SHELL = /bin/sh

# The CMake executable.
CMAKE_COMMAND = /usr/bin/cmake

# The command to remove a file.
RM = /usr/bin/cmake -E remove -f

# Escaping for special characters.
EQUALS = =

# The top-level source directory on which CMake was run.
CMAKE_SOURCE_DIR = /var/lib/jenkins/workspace/Googletest-gcov-gtest

# The top-level build directory on which CMake was run.
CMAKE_BINARY_DIR = /var/lib/jenkins/workspace/Googletest-gcov-gtest

# Include any dependencies generated for this target.
include googletest/CMakeFiles/sample5_unittest.dir/depend.make

# Include the progress variables for this target.
include googletest/CMakeFiles/sample5_unittest.dir/progress.make

# Include the compile flags for this target's objects.
include googletest/CMakeFiles/sample5_unittest.dir/flags.make

googletest/CMakeFiles/sample5_unittest.dir/samples/sample5_unittest.cc.o: googletest/CMakeFiles/sample5_unittest.dir/flags.make
googletest/CMakeFiles/sample5_unittest.dir/samples/sample5_unittest.cc.o: googletest/samples/sample5_unittest.cc
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/var/lib/jenkins/workspace/Googletest-gcov-gtest/CMakeFiles --progress-num=$(CMAKE_PROGRESS_1) "Building CXX object googletest/CMakeFiles/sample5_unittest.dir/samples/sample5_unittest.cc.o"
	cd /var/lib/jenkins/workspace/Googletest-gcov-gtest/googletest && /usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -o CMakeFiles/sample5_unittest.dir/samples/sample5_unittest.cc.o -c /var/lib/jenkins/workspace/Googletest-gcov-gtest/googletest/samples/sample5_unittest.cc

googletest/CMakeFiles/sample5_unittest.dir/samples/sample5_unittest.cc.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/sample5_unittest.dir/samples/sample5_unittest.cc.i"
	cd /var/lib/jenkins/workspace/Googletest-gcov-gtest/googletest && /usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /var/lib/jenkins/workspace/Googletest-gcov-gtest/googletest/samples/sample5_unittest.cc > CMakeFiles/sample5_unittest.dir/samples/sample5_unittest.cc.i

googletest/CMakeFiles/sample5_unittest.dir/samples/sample5_unittest.cc.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/sample5_unittest.dir/samples/sample5_unittest.cc.s"
	cd /var/lib/jenkins/workspace/Googletest-gcov-gtest/googletest && /usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /var/lib/jenkins/workspace/Googletest-gcov-gtest/googletest/samples/sample5_unittest.cc -o CMakeFiles/sample5_unittest.dir/samples/sample5_unittest.cc.s

googletest/CMakeFiles/sample5_unittest.dir/samples/sample1.cc.o: googletest/CMakeFiles/sample5_unittest.dir/flags.make
googletest/CMakeFiles/sample5_unittest.dir/samples/sample1.cc.o: googletest/samples/sample1.cc
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/var/lib/jenkins/workspace/Googletest-gcov-gtest/CMakeFiles --progress-num=$(CMAKE_PROGRESS_2) "Building CXX object googletest/CMakeFiles/sample5_unittest.dir/samples/sample1.cc.o"
	cd /var/lib/jenkins/workspace/Googletest-gcov-gtest/googletest && /usr/bin/c++  $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -o CMakeFiles/sample5_unittest.dir/samples/sample1.cc.o -c /var/lib/jenkins/workspace/Googletest-gcov-gtest/googletest/samples/sample1.cc

googletest/CMakeFiles/sample5_unittest.dir/samples/sample1.cc.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/sample5_unittest.dir/samples/sample1.cc.i"
	cd /var/lib/jenkins/workspace/Googletest-gcov-gtest/googletest && /usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /var/lib/jenkins/workspace/Googletest-gcov-gtest/googletest/samples/sample1.cc > CMakeFiles/sample5_unittest.dir/samples/sample1.cc.i

googletest/CMakeFiles/sample5_unittest.dir/samples/sample1.cc.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/sample5_unittest.dir/samples/sample1.cc.s"
	cd /var/lib/jenkins/workspace/Googletest-gcov-gtest/googletest && /usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /var/lib/jenkins/workspace/Googletest-gcov-gtest/googletest/samples/sample1.cc -o CMakeFiles/sample5_unittest.dir/samples/sample1.cc.s

# Object files for target sample5_unittest
sample5_unittest_OBJECTS = \
"CMakeFiles/sample5_unittest.dir/samples/sample5_unittest.cc.o" \
"CMakeFiles/sample5_unittest.dir/samples/sample1.cc.o"

# External object files for target sample5_unittest
sample5_unittest_EXTERNAL_OBJECTS =

googletest/sample5_unittest: googletest/CMakeFiles/sample5_unittest.dir/samples/sample5_unittest.cc.o
googletest/sample5_unittest: googletest/CMakeFiles/sample5_unittest.dir/samples/sample1.cc.o
googletest/sample5_unittest: googletest/CMakeFiles/sample5_unittest.dir/build.make
googletest/sample5_unittest: lib/libgtest_maind.a
googletest/sample5_unittest: lib/libgtestd.a
googletest/sample5_unittest: googletest/CMakeFiles/sample5_unittest.dir/link.txt
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --bold --progress-dir=/var/lib/jenkins/workspace/Googletest-gcov-gtest/CMakeFiles --progress-num=$(CMAKE_PROGRESS_3) "Linking CXX executable sample5_unittest"
	cd /var/lib/jenkins/workspace/Googletest-gcov-gtest/googletest && $(CMAKE_COMMAND) -E cmake_link_script CMakeFiles/sample5_unittest.dir/link.txt --verbose=$(VERBOSE)

# Rule to build all files generated by this target.
googletest/CMakeFiles/sample5_unittest.dir/build: googletest/sample5_unittest

.PHONY : googletest/CMakeFiles/sample5_unittest.dir/build

googletest/CMakeFiles/sample5_unittest.dir/clean:
	cd /var/lib/jenkins/workspace/Googletest-gcov-gtest/googletest && $(CMAKE_COMMAND) -P CMakeFiles/sample5_unittest.dir/cmake_clean.cmake
.PHONY : googletest/CMakeFiles/sample5_unittest.dir/clean

googletest/CMakeFiles/sample5_unittest.dir/depend:
	cd /var/lib/jenkins/workspace/Googletest-gcov-gtest && $(CMAKE_COMMAND) -E cmake_depends "Unix Makefiles" /var/lib/jenkins/workspace/Googletest-gcov-gtest /var/lib/jenkins/workspace/Googletest-gcov-gtest/googletest /var/lib/jenkins/workspace/Googletest-gcov-gtest /var/lib/jenkins/workspace/Googletest-gcov-gtest/googletest /var/lib/jenkins/workspace/Googletest-gcov-gtest/googletest/CMakeFiles/sample5_unittest.dir/DependInfo.cmake --color=$(COLOR)
.PHONY : googletest/CMakeFiles/sample5_unittest.dir/depend
