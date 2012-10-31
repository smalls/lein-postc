# lein-postc

A small statically-generated blog system. This was based on a dynamic database
backed system that I was working on, but that project
([smallblog](https://github.com/smalls/smallblog)) became unwieldy.

Key features include:

- Markdown-formatted posts.
- Nothing else.


## Getting Started

Start by writing your source posts, in [Markdown](http://daringfireball.net/projects/markdown/syntax).
They should be in src/posts, formatted as 'YYYY-MM-DD-title with spaces.md'.

Add the dependency to your project.clj:

	:plugins [[lein-postc "0.1.0"]]

To generate the pages, run

	lein postc

Generated files will be in target/web/static, by default. Add that to what
you're using to serve your other web pages.


## Sample Project

I have a sample up at <http://b.splendous.net/>. You can see the code on
[github](http://www.github.com): <https://github.com/smalls/b.splendous.net>.

This sample has some posts, uses [compojure](https://github.com/weavejester/compojure)
& [ring](https://github.com/mmcgrana/ring) to serve the static files.


### Heroku

It also has a bit of magic to make the [heroku](http://www.heroku.com/) push
just work. For that, I added an extra step to the build to run lein-postc by
adding a bin/build file:

	#!/bin/sh
	lein genblog


## License

Copyright (C) 2012 Matt Small

Distributed under the Eclipse Public License, the same as Clojure.
