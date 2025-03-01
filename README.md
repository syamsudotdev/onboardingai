# Onboarding AI Android App
## Prerequisites
- Java Development Kit (JDK) 17+

## Building the project
1. `git clone https://github.com/syamsudotdev/onboardingai.git`
2. `cd onboardingai`
3. `./gradlew build`

## Running the project
1. `./gradlew :app:installDebug`

## Formatting
This project uses [diffplug/spotless](https://github.com/diffplug/spotless/) with [ktfmt](https://github.com/facebook/ktfmt/) for formatting Kotlin code.

To format the code, run `./gradlew spotlessApply`.
