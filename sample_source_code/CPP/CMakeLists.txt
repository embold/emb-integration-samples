cmake_minimum_required(VERSION 3.16)
project(AnimalShelter CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

add_executable(animal_shelter
    src/Animal.cpp
    src/Dog.cpp
    src/Cat.cpp
    src/main.cpp
)

target_include_directories(animal_shelter PRIVATE src)
