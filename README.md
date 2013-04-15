# Processing Tools

These processing tools are meant to help [Processing](http://processing.org) based coders to get their work done quicker with a couple of simple utilities:

## [Bootstrap](http://poifox.com/bootstrap):

The Bootstrap tool for Processing 2.0+ lets coders load a predefined template to their current Processing sketch as a base for further coding, removing the tedious process of writing base Procesing style code like setup(), draw() and more. Bootstrap superceedes the old InitSketch tool, and build upon the original code with better coding style and encapsulation.

Bootstrap goes along very well with this other tool:

## [Templater](http://poifox.com/templater):

The Templater tool for Processing 2.0+ is the perfect companion for Bootstrap. It allows the coder to save the currently visible sketch as a template in the templates folder, which can then be used by Bootstrap to initialize sketches with different templates. Templater superceedes the old SaveTemplate tool, and builds upon the original code with better coding style and encapsulation.

### Instructions:

Detailed instructions on [Bootstrap](http://poifox.com/bootstrap)'s and [Templater](http://poifox.com/templater)'s' mini-sites.

### Building

These tools have been built successfuly on Ubuntu 12.10 and Mac OS 10.6.8 using the ant build. There are two build systems setup already for them in the processing-tools.sublime-project file for those fond of Sublime Text 2.

To build them just `cd processing-tools/toolname/resources` and run ant on each tool:

`ant -f build.xml`

The tools are already setup for Eclipse, but classpaths may fail on Mac OS X and/or Windows because the project was setup on my Ubuntu machine where Processing is installed on `/opt/processing`

A quick change of the `classpath.local.location` property in each `build.properties` file so they point to the correct classpath for `pde.jar` and `core.jar` is enough for ant to build them successfully (on Mac OS X 10.6.8 and with `project.compile=fast`) from the command line or from Sublime Text 2.

---

### Future:

These tools are pretty much set in their ways, some maintenance fixes will be done in the future but not much will change with them. I initially coded them to behave _exactly_ like they do and they were of good use while I was a raging Processing user. Now I spend most of my time writing PHP and C++ and I don't see myself going back to P5, but I will give maintenance to them when needed :)

If you have improvements you'd like to submit please feel free to fork this repository and send me your pull requests with your changes for everybody else to enjoy!

### Change Log:

1.01 - Maintenance release for processing.org standards compliance.

1.0 - Re-release of the tools under new project names.
