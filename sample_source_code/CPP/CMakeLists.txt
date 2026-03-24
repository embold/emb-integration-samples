cmake_minimum_required(VERSION 3.16)
project(AnimalShelter CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# ── Production library (shared between main and tests) ───────────────────────
add_library(animal_lib STATIC
    src/Animal.cpp
    src/Dog.cpp
    src/Cat.cpp
)
target_include_directories(animal_lib PUBLIC src)

# ── Main executable ──────────────────────────────────────────────────────────
add_executable(animal_shelter src/main.cpp)
target_link_libraries(animal_shelter PRIVATE animal_lib)

# ── GoogleTest via FetchContent ──────────────────────────────────────────────
include(FetchContent)
FetchContent_Declare(
    googletest
    URL https://github.com/google/googletest/archive/refs/tags/v1.14.0.zip
    DOWNLOAD_EXTRACT_TIMESTAMP TRUE
)
# Prevent overriding the parent project's compiler/linker settings on Windows
set(gtest_force_shared_crt ON CACHE BOOL "" FORCE)
FetchContent_MakeAvailable(googletest)

# ── Test executable ──────────────────────────────────────────────────────────
enable_testing()

add_executable(animal_tests
    tests/MathUtilsTest.cpp
    tests/DogTest.cpp
    tests/CatTest.cpp
    tests/ShelterTest.cpp
)
target_include_directories(animal_tests PRIVATE src)
target_link_libraries(animal_tests PRIVATE animal_lib GTest::gtest_main)

include(GoogleTest)
gtest_discover_tests(animal_tests)
