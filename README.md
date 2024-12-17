# mCards Android Accounts SDK Demo App

The mCards android Accounts SDK encapsulates the following functionality:

1. Link bank accounts
2. Link credit accounts
3. Deactivate accounts
4. Reorder account priority

# Usage
Implementing apps MUST override this string value for auth0 to work:

<string name="auth0_domain">your value here</string>

Theis value is gotten from the mCards team after setting up the client's auth0 instance.

You must then also update the manifest placeholders in the build.gradle file:

e.g. addManifestPlaceholders(mapOf("auth0Domain" to "@string/auth0_domain", "auth0Scheme" to "your app ID"))


# Importing the Auth SDK
Add the following to your module-level build.gradle:

Groovy:
```
implementation "com.mcards.sdk:accounts:$latestVersion"
```

Kotlin:
```
implementation("com.mcards.sdk:accounts:$latestVersion")
```

And the following to the project settings.gradle:
```
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

        maven {
            url = uri("https://maven.pkg.github.com/Wantsa/sdk-accounts-android")
            credentials {
                username = GITHUB_USERNAME
                password = GITHUB_TOKEN
            }
        }
    }
}
```

# Documentation
\\\\\Add documentation links here/////
