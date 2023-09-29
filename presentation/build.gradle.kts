plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("com.google.protobuf")
    kotlin("kapt")
}

android {
    namespace = "com.orot.menuboss_tv.presentation"
    compileSdk = AppConfig.compileSdk

    defaultConfig {
        minSdk = AppConfig.minSdk
    }

    buildFeatures.compose = true

    compileOptions {
        sourceCompatibility = AppConfig.javaVersion
        targetCompatibility = AppConfig.javaVersion
    }
    kotlinOptions {
        jvmTarget = AppConfig.jvmTarget
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Versions.Compose.compiler
    }
}

dependencies {

    api(project(":domain"))

    Libraries.AndroidX.run {
        implementation(core)
    }

    Libraries.Compose.run {
        implementation(uiTooling)
        implementation(activity)
        implementation(tvFoundation)
        implementation(tvMaterial)
        implementation(coil)
        implementation(navigation)
        implementation(viewModel)
        implementation(bom)
        implementation(contraintLayout)
    }

    Libraries.Google.run {
        implementation(guava)
        implementation(zxing)
        implementation(fcm)
    }

    Libraries.Rive.run {
        implementation(rive)
    }

    Libraries.Gson.run {
        implementation(gson)
    }

    Libraries.Protobuf.run {
        implementation(grpcStub)
        implementation(grpcProtobufLite)
        implementation(kotlinStub)
        implementation(protobufKotlinLite)
    }

    Kapts.Hilt.run {
        kapt(daggerHiltCompiler)
        kapt(daggerHiltAndroidCompiler)
        kapt(daggerHiltAndroid)
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${Versions.Protobuf.protobufVersion}"
    }
    plugins {
        create("java") {
            artifact = "io.grpc:protoc-gen-grpc-java:${Versions.Protobuf.grpcVersion}"
        }
        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:${Versions.Protobuf.grpcVersion}"
        }
        create("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:${Versions.Protobuf.grpcKotlinVersion}:jdk8@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                create("java") {
                    option("lite")
                }
                create("grpc") {
                    option("lite")
                }
                create("grpckt") {
                    option("lite")
                }
            }
            it.builtins {
                create("kotlin") {
                    option("lite")
                }
            }
        }
    }
}

kapt {
    correctErrorTypes = true
}