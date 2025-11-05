# 📦 Publishing Guide for polargx-android-sdk-dev

## Publishing to JitPack (Recommended)

JitPack is the easiest way to publish your Android library directly from GitHub.

### Prerequisites
- Your repository must be public on GitHub
- You must have push access to the repository
- Your library must build successfully

### Step-by-Step Guide

#### 1. Ensure Everything is Committed
```bash
git status  # Check for uncommitted changes
git add .   # Stage all changes
git commit -m "Release version X.X.X"
```

#### 2. Create a Git Tag
The tag name will be used as the version number in JitPack.

```bash
# Create an annotated tag (recommended)
git tag -a v3.1.7 -m "Release version 3.1.7"

# Or create a lightweight tag
git tag v3.1.7
```

**Important:** The tag format should be `vX.X.X` (with 'v' prefix) or `X.X.X`

#### 3. Push to GitHub
```bash
# Push your commits
git push origin 3.1.7

# Push the tag
git push origin v3.1.7

# Or push all tags at once
git push origin --tags
```

#### 4. Trigger JitPack Build

Go to: `https://jitpack.io/#polargx/polargx-android-sdk-dev`

- JitPack will automatically detect your new tag
- Click on "Get it" or the version number
- JitPack will build your library (this may take a few minutes)
- Once the build status shows ✓ (green checkmark), your library is published!

#### 5. Using Your Published Library

Users can now add your library to their Android project:

**Step 1: Add JitPack repository in `settings.gradle` or `settings.gradle.kts`:**
```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

**Step 2: Add the dependency in `build.gradle.kts`:**
```kotlin
dependencies {
    implementation("com.github.polargx:polargx-android-sdk-dev:3.1.7")
}
```

---

## Quick Command Reference

### Publishing a New Version

```bash
# 1. Update version in polargx/build.gradle.kts
# 2. Commit changes
git add polargx/build.gradle.kts
git commit -m "Release version X.X.X"

# 3. Create and push tag
git tag -a vX.X.X -m "Release version X.X.X"
git push origin YOUR_BRANCH_NAME
git push origin vX.X.X

# 4. Go to https://jitpack.io/#polargx/polargx-android-sdk-dev
# 5. Wait for build to complete
```

### Deleting a Tag (if needed)

```bash
# Delete local tag
git tag -d vX.X.X

# Delete remote tag
git push origin :refs/tags/vX.X.X
```

---

## Alternative: Publishing to Maven Central

If you prefer Maven Central instead of JitPack, you'll need to:

1. Register for a Sonatype OSSRH account
2. Configure GPG signing
3. Add additional plugins and configuration
4. Set up credentials

This is more complex but provides better control and is the "official" Maven repository.

**Required additions to `build.gradle.kts`:**
```kotlin
plugins {
    id("maven-publish")
    id("signing")
}

// Add POM metadata
publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "io.github.polargx"
            artifactId = "polargx-android-sdk"
            version = "3.1.7"
            
            afterEvaluate {
                from(components["release"])
            }
            
            pom {
                name.set("PolarGX Android SDK")
                description.set("Android SDK for PolarGX")
                url.set("https://github.com/polargx/polargx-android-sdk-dev")
                
                licenses {
                    license {
                        name.set("Apache License 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0")
                    }
                }
                
                developers {
                    developer {
                        id.set("polargx")
                        name.set("PolarGX Team")
                        email.set("your-email@example.com")
                    }
                }
                
                scm {
                    connection.set("scm:git:git://github.com/polargx/polargx-android-sdk-dev.git")
                    developerConnection.set("scm:git:ssh://github.com/polargx/polargx-android-sdk-dev.git")
                    url.set("https://github.com/polargx/polargx-android-sdk-dev")
                }
            }
        }
    }
    
    repositories {
        maven {
            name = "OSSRH"
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = project.findProperty("ossrhUsername") as String? ?: System.getenv("OSSRH_USERNAME")
                password = project.findProperty("ossrhPassword") as String? ?: System.getenv("OSSRH_PASSWORD")
            }
        }
    }
}

signing {
    sign(publishing.publications["release"])
}
```

---

## Troubleshooting

### JitPack build fails
- Check the JitPack build logs for errors
- Ensure your library builds locally: `./gradlew :polargx:build`
- Make sure all dependencies are publicly available
- Check that minimum SDK and compile SDK versions are compatible

### Tag already exists
```bash
# Delete and recreate the tag
git tag -d v3.1.7
git push origin :refs/tags/v3.1.7
git tag -a v3.1.7 -m "Release version 3.1.7"
git push origin v3.1.7
```

### JitPack shows wrong version
- Clear JitPack cache by clicking "Clear" button on JitPack page
- Delete and recreate the tag
- Make sure the version in build.gradle.kts matches the tag

---

## Best Practices

1. **Use Semantic Versioning:** `MAJOR.MINOR.PATCH` (e.g., 3.1.7)
   - MAJOR: Breaking changes
   - MINOR: New features (backward compatible)
   - PATCH: Bug fixes

2. **Always test before publishing:**
   ```bash
   ./gradlew :polargx:build
   ./gradlew :polargx:test
   ```

3. **Create a CHANGELOG.md** to track changes between versions

4. **Update documentation** before each release

5. **Use annotated tags** with descriptive messages

---

## Current Version Information

- **Current Version:** 3.1.7
- **GroupId:** com.github.polargx
- **ArtifactId:** polargx-android-sdk-dev
- **Repository:** JitPack

