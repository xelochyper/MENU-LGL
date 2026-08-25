cmake_minimum_required(VERSION 3.22.1)

project("native-lib")

# Configure C++17 standard
set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Include directories for headers
include_directories(
    ${CMAKE_CURRENT_SOURCE_DIR}
    ${CMAKE_CURRENT_SOURCE_DIR}/include
)

# Define the shared library 'native-lib'
add_library(
    native-lib
    SHARED
    main.cpp
)

# Find Android NDK system log library
find_library(
    log-lib
    log
)

# Link libraries to native-lib
target_link_libraries(
    native-lib
    ${log-lib}
)
