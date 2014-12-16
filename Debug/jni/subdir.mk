################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
../jni/nonfree_jni.cpp 

OBJS += \
./jni/nonfree_jni.o 

CPP_DEPS += \
./jni/nonfree_jni.d 


# Each subdirectory must supply rules for building sources it contributes
jni/%.o: ../jni/%.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C++ Compiler'
	g++ -I/Users/gaoboning/Downloads/OpenCV-2.4.9-android-sdk -I/Users/gaoboning/NDK/platforms/android-14/arch-mips/usr/include -I/Users/gaoboning/NDK/toolchains/arm-linux-androideabi-4.8/prebuilt/darwin-x86_64/lib/gcc/arm-linux-androideabi/4.8/include -I/Users/gaoboning/NDK/platforms/android-14/arch-arm/usr/include/linux -I/Users/gaoboning/NDK/sources/cxx-stl/gnu-libstdc++/4.8/libs/armeabi-v7a -I/Users/gaoboning/NDK/sources/cxx-stl/gnu-libstdc++/4.8/include -I/Users/gaoboning/NDK/sources/cxx-stl/gnu-libstdc++/4.8/include/tr1 -I/Users/gaoboning/NDK/platforms/android-14/arch-arm/usr/include -I/Users/gaoboning/Downloads/OpenCV-2.4.9-android-sdk/sdk/native/jni/include -O0 -g3 -Wall -c -fmessage-length=0 -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


