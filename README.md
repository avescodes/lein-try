# lein-try [![Build Status](https://travis-ci.org/rkneufeld/lein-try.png)](https://travis-ci.org/rkneufeld/lein-try)

A [Leiningen](https://github.com/technomancy/leiningen) plugin for trying out
Clojure libraries in a REPL without creating a project or adding them to an existing
project.

## Usage

#### Leiningen ([via Clojars](https://clojars.org/lein-try))

Put the following into the `:plugins` vector of the `:user` profile in your `~/.lein/profiles.clj`:

```clojure
[lein-try "0.3.0"]
```

This plugin requires Leiningen >= 2.1.3.

#### Command Line

You can use `lein-try` to open a REPL with any dependencies you specify loaded and ready to use.

```bash
$ lein try [clj-time "0.5.1"]
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

You can even leave off the version number and leiningen will pull the most recently released version! (Thanks @xsc)

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

    lein try [io.rkn/conformity "0.2.1"] [com.datomic/datomic-free "0.8.4020.26"]
    lein try io.rkn/conformity 0.2.1
    lein try io.rkn/conformity # This uses the most recent version

Arguments: ([& args])
```

#### In Emacs

Miss Emacs integration while `lein try`ing? In your `*scratch*` buffer, set your `inferior-lisp-program` var...

`(setq inferior-lisp-program "lein try tentacles")`

and then launch `M-x inferior-lisp`.

#### On ZSH

[ZSH](zsh.org) has this fun feature where `[..]` is a special type of pattern matching–this kind of sucks for this plugin, no?

You have two options to get around this:

1. Use `noglob` before `lein try`. (If you never use `[]` matching with lein, just `alias lein="noglob lein"` in your zshrc.)
2. Copy-paste *only* the library and dependency (i.e. `lein try clj-time "0.5.1"`)

## Contributions

Contributions are more than welcome. Fire away in an [issue](../../issues/new) or pull-request.

## License

Copyright &copy; 2013 Ryan Neufeld

Distributed under the Eclipse Public License, the same as Clojure.

