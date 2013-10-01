# lein-try [![Build Status](https://travis-ci.org/rkneufeld/lein-try.png)](https://travis-ci.org/rkneufeld/lein-try)

A [Leiningen](https://github.com/technomancy/leiningen) plugin for trying out
Clojure libraries in a REPL without creating a project or adding them to an existing
project.

Special thanks to contributors [@xsc](https://github.com/xsc) and [@seancorfield](https://github.com/seancorfield) for making lein-try amazing.

## Usage

#### Leiningen ([via Clojars](https://clojars.org/lein-try))

Put the following into the `:plugins` vector of the `:user` profile in your `~/.lein/profiles.clj`:

```clojure
[lein-try "0.4.0"]
```

This plugin requires Leiningen >= 2.1.3.

#### Command Line

You can use `lein-try` to open a REPL with any dependencies you specify loaded and ready to use.

```bash
$ lein try clj-time "0.5.1"
Fetching dependencies... (takes a while the first time)
lein-try loaded [clj-time "0.5.1"]

nREPL server started on port 57036
REPL-y 0.2.0
Clojure 1.5.1
    Docs: (doc function-name-here)
          (find-doc "part-of-name-here")
  Source: (source function-name-here)
 Javadoc: (javadoc java-object-or-class-here)
    Exit: Control+D or (exit) or (quit)

user=>
```

You can even leave off the version number and leiningen will pull the most
recently released version! (Thanks @xsc.) The command `lein try clj-time`
evaluates as `lein try clj-time "RELEASE"`. Feel free to mix versioned and
unversioned dependencies–we're good like that.

```bash
$ lein try clj-time
#...
user=>
```

To see available options, call `lein help try`:

```bash
$ lein help try
Launch REPL with specified dependencies available.

  Usage:

    lein try io.rkn/conformity "0.2.1" com.datomic/datomic-free "0.8.4020.26"
    lein try io.rkn/conformity 0.2.1
    lein try io.rkn/conformity # This uses the most recent version

Arguments: ([& args])
```

#### In Emacs

Miss Emacs integration while `lein try`ing? In your `*scratch*` buffer, set your `inferior-lisp-program` var...

`(setq inferior-lisp-program "lein try tentacles")`

and then launch `M-x inferior-lisp`.

## Contributions

Contributions are more than welcome. Fire away in an [issue](../../issues/new) or pull-request.

## License

Copyright &copy; 2013 Ryan Neufeld

Distributed under the Eclipse Public License, the same as Clojure.

