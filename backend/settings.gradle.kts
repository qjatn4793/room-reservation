plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0" // 또는 최신
}

rootProject.name = "room-reservation-backend"

include(":gateway")
include(":services:room")
include(":services:pay")
include(":common")
include(":util")
