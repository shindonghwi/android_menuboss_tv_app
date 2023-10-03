plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.protobuf")
}

android {
    namespace = "com.orotcode.menuboss.grpc"
    compileSdk = 34

    defaultConfig {
        minSdk = AppConfig.minSdk
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = AppConfig.javaVersion
        targetCompatibility = AppConfig.javaVersion
    }
    kotlinOptions {
        jvmTarget = AppConfig.jvmTarget
    }
}

dependencies {
    Libraries.Coroutine.apply {
        implementation(core)
    }

    Libraries.Protobuf.run {
        api(grpcCore)
        api(grpcOkhttp)
        api(grpcProtobufLite)
        api(kotlinStub)
        api(protobufKotlinLite)
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