# kz-pompei-fui

> File-based user interface controls for small Java tools and services.

[![Java](https://img.shields.io/badge/Java-21-blue.svg)](#requirements)
[![Gradle](https://img.shields.io/badge/build-Gradle-green.svg)](#build-and-test)
[![TestNG](https://img.shields.io/badge/tests-TestNG-orange.svg)](#build-and-test)
[![Version](https://img.shields.io/badge/version-0.0.2-lightgrey.svg)](versions/version.txt)

`kz-pompei-fui` exposes simple controls as files in a directory. It lets a
running Java process react to file operations without opening a GUI window or
adding an HTTP endpoint.

- A button is a `.btn` file. Delete it to trigger a click.
- An editor is a `.edit` file. Edit its text to update a value.
- A checkbox is represented by `.YES` and `.NO` files. Toggle it by deleting the
  current file.
- A close button file stops the application loop when deleted.

## Contents

- [Quick Start](#quick-start)
- [Controls](#controls)
- [Configuration](#configuration)
- [Installation](#installation)
- [Requirements](#requirements)
- [Build And Test](#build-and-test)
- [Project Layout](#project-layout)

## Quick Start

```java
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import kz.pompei.fui.Disconnector;
import kz.pompei.fui.Fui;
import kz.pompei.fui.FuiCheckbox;
import kz.pompei.fui.FuiEditor;

public class App {
  public static void main(String[] args) {
    Fui fui = Fui.builder()
                 .rootDir(Path.of("build/application"))
                 .build();

    List<Disconnector> disconnectors = new ArrayList<>();

    disconnectors.add(fui.button("Say_Hello")
                         .click(() -> System.out.println("Hello")));

    FuiEditor message = fui.editor("Message");
    message.value.set("");
    disconnectors.add(message.change(() -> System.out.println(message.value.get())));

    FuiCheckbox enabled = fui.checkbox("Enabled");
    disconnectors.add(enabled.change(() -> System.out.println(enabled.value.is())));

    fui.go();

    disconnectors.forEach(Disconnector::disconnect);
    message.remove();
    enabled.remove();
  }
}
```

After startup, the root directory contains files like:

```text
build/application/
  Close_Application.btn
  Say_Hello.btn
  Message.edit
  Enabled.NO
```

Delete `Say_Hello.btn` to fire the button handler. Edit `Message.edit` to fire
the editor change handler. Delete either checkbox state file to toggle the
checkbox. Delete `Close_Application.btn` to stop `fui.go()`.

## Controls

### Buttons

```java
Disconnector disconnector = fui.button("tasks/Rebuild")
                             .click(() -> rebuild());
```

The button creates `tasks/Rebuild.btn` under the configured root directory. When
the file is deleted, FUI recreates it and calls registered click handlers.

Button visibility can be changed at runtime:

```java
var button = fui.button("Admin_Action");
button.visibility.set(false);
button.visibility.set(true);
```

### Editors

```java
FuiEditor editor = fui.editor("Settings/Name");

editor.value.set("default value");

Disconnector disconnector = editor.change(() -> {
  String value = editor.value.get();
  saveName(value);
});
```

The editor uses a `.edit` file and stores its latest value in `.fui/cache`.
Programmatic updates write both the visible file and cache file.

### Checkboxes

```java
FuiCheckbox checkbox = fui.checkbox("Feature_Enabled");

checkbox.value.set(true);

Disconnector disconnector = checkbox.change(() -> {
  boolean enabled = checkbox.value.is();
  applyFeatureState(enabled);
});
```

A checkbox maintains `Feature_Enabled.YES` and `Feature_Enabled.NO`. Exactly one
state file is kept by the library. Removing the current state file toggles the
value and fires change handlers.

## Configuration

Create an instance through `Fui.builder()`:

```java
Fui fui = Fui.builder()
             .rootDir(Path.of("/tmp/my-app-fui"))
             .stopApplicationBtnName("Stop")
             .extensionBtn(".button")
             .extensionEdit(".txt")
             .surveyLoopSleepMs(100)
             .errHandler(Throwable::printStackTrace)
             .build();
```

| Option                   | Default             | Description                                      |
|--------------------------|---------------------|--------------------------------------------------|
| `rootDir`                | required            | Directory where FUI control files are created.   |
| `stopApplicationBtnName` | `Close_Application` | Name of the stop file, without button extension. |
| `extensionBtn`           | `.btn`              | File extension used for buttons.                 |
| `extensionEdit`          | `.edit`             | File extension used for editors.                 |
| `surveyLoopSleepMs`      | `100`               | Delay between file-system polling iterations.    |
| `errHandler`             | `printStackTrace`   | Handler for exceptions thrown by callbacks.      |

`fui.go()` runs the polling loop on the current thread. Start it on a dedicated
thread if the rest of the application must continue running concurrently.

## Installation

### Gradle

```groovy
repositories {
  mavenCentral()
}

dependencies {
  implementation "kz.pompei.fui:kz-pompei-fui:0.0.2"
}
```

### Maven

```xml
<dependency>
  <groupId>kz.pompei.fui</groupId>
  <artifactId>kz-pompei-fui</artifactId>
  <version>0.0.2</version>
</dependency>
```

For local development, publish the module to the local Maven repository:

```bash
./gradlew :kz-pompei-fui:publishToMavenLocal
```

## Requirements

- Java 21
- Gradle wrapper from this repository

## Build And Test

Compile the library:

```bash
./gradlew :kz-pompei-fui:compileJava
```

Run the interactive TestNG smoke test:

```bash
./gradlew :kz-pompei-fui:test
```

The current test starts `fui.go()` and waits for `build/application/Close_Application.btn`
to be deleted.

## Project Layout

```text
kz-pompei-fui/
  src/       Main Java sources
  test_src/  TestNG tests
publication/ Publication helper module
buildSrc/    Local Gradle plugin aliases
versions/    Version file used by Gradle
```
