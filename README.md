# mCards Android Accounts SDK Demo App

The mCards android Accounts SDK encapsulates the following functionality:

1. Link bank accounts
2. Link credit accounts
3. Deactivate accounts
4. Reorder account priority

# Integration
Implementing apps MUST override this string value for auth0 to work:

```<string name="auth0_domain">your value here</string>```

These values are gotten from the mCards team after setting up the client's auth0 instance.

You must then also update the manifest placeholders in the build.gradle file:

e.g. ```addManifestPlaceholders(mapOf("auth0Domain" to "@string/auth0_domain", "auth0Scheme" to "your app ID"))```

No unique steps are required to integrate with the Cards SDK.


# Importing the Accounts SDK
The mCards android SDKs are provided via a bill of materials. Add the following to your module-level build.gradle:

Groovy:
```
implementation(platform("com.mcards.sdk:bom:$latestVersion"))
implementation "com.mcards.sdk:accounts"
//implementation "com.mcards.sdk:auth" //only if also using the auth sdk as a token provider
```

Kotlin:
```
implementation(platform("com.mcards.sdk:bom:$latestVersion"))
implementation("com.mcards.sdk:accounts")
//implementation("com.mcards.sdk:auth") //only if also using the auth sdk as a token provider
```

And the following to the project settings.gradle (groovy):
```
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

        maven {
            url = uri("https://maven.pkg.github.com/mcards/sdk-bom-android")
            credentials {
                username = GITHUB_USERNAME
                password = GITHUB_TOKEN
            }
        }
    }
}
```

# Test User
A basic user has been set up using a free SMS service. This user has the minimum amount of data needed to login and perform most SDK operations.

The user's phone number is:
+1 405-293-8132

and SMS codes are received here:
https://receive-sms.cc/US-Phone-Number/14052938132

# Documentation
\\\\\Add documentation links here/////
