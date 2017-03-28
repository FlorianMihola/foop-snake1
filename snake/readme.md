**Manual steps:** (For running on eclipse)

* You must have the Maven Eclipse integration plugin installed (m2e)
* Import the Maven project
* Right-click on `Game`, Debug as, Java application
* This will fail with `java.lang.UnsatifsiedLinkError`
* Run `mvn package` once, the native libraries will get copied in `target/natives`
* Edit your debug configuration (menu Run, Debug configurations...), on the "Arguments" tab, "VM Arguments" field, enter `-Djava.library.path=target/natives`
* Click on "Debug" and you're all set !