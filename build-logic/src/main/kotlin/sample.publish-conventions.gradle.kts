plugins {
    `maven-publish`
}

publishing {
    publications {
        create<MavenPublication>("gpr") {
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/youngjinsin-colosseum/packages-and-release-please-test")
            credentials {
                username = providers.gradleProperty("gpr.user").orNull ?: System.getenv("GPR_USER")
                password = providers.gradleProperty("gpr.token").orNull ?: System.getenv("GPR_TOKEN")
            }
        }
    }
}
