# Distributed under the OSI-approved BSD 3-Clause License.  See accompanying
# file Copyright.txt or https://cmake.org/licensing for details.

cmake_minimum_required(VERSION 3.5)

file(MAKE_DIRECTORY
  "/Users/nilesh/nilesh/source_codes/git/emb-integration-samples/sample_source_code/CPP/build/_deps/googletest-src"
  "/Users/nilesh/nilesh/source_codes/git/emb-integration-samples/sample_source_code/CPP/build/_deps/googletest-build"
  "/Users/nilesh/nilesh/source_codes/git/emb-integration-samples/sample_source_code/CPP/build/_deps/googletest-subbuild/googletest-populate-prefix"
  "/Users/nilesh/nilesh/source_codes/git/emb-integration-samples/sample_source_code/CPP/build/_deps/googletest-subbuild/googletest-populate-prefix/tmp"
  "/Users/nilesh/nilesh/source_codes/git/emb-integration-samples/sample_source_code/CPP/build/_deps/googletest-subbuild/googletest-populate-prefix/src/googletest-populate-stamp"
  "/Users/nilesh/nilesh/source_codes/git/emb-integration-samples/sample_source_code/CPP/build/_deps/googletest-subbuild/googletest-populate-prefix/src"
  "/Users/nilesh/nilesh/source_codes/git/emb-integration-samples/sample_source_code/CPP/build/_deps/googletest-subbuild/googletest-populate-prefix/src/googletest-populate-stamp"
)

set(configSubDirs )
foreach(subDir IN LISTS configSubDirs)
    file(MAKE_DIRECTORY "/Users/nilesh/nilesh/source_codes/git/emb-integration-samples/sample_source_code/CPP/build/_deps/googletest-subbuild/googletest-populate-prefix/src/googletest-populate-stamp/${subDir}")
endforeach()
if(cfgdir)
  file(MAKE_DIRECTORY "/Users/nilesh/nilesh/source_codes/git/emb-integration-samples/sample_source_code/CPP/build/_deps/googletest-subbuild/googletest-populate-prefix/src/googletest-populate-stamp${cfgdir}") # cfgdir has leading slash
endif()
