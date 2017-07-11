var gulp        = require("gulp");
var runSequence = require("run-sequence");
var riot        = require("gulp-riot");
var concat      = require("gulp-concat");
var uglify      = require("gulp-uglify");
var sass        = require("gulp-sass");

gulp.task("riot", function() {
  var stream = gulp.src("riot/tags/*.tag")
    .on("error", function() {
      console.log("==============> hoge");
    })
    .pipe(riot({
      compact: true,
      type: "typescript"
    }))
    .pipe(gulp.dest("riot/compiled"));
  return stream;
});

gulp.task("concat", function() {
  return gulp.src("riot/compiled/*.js")
    .pipe(concat("tag.js"))
    .pipe(gulp.dest("public/javascripts"));
});

gulp.task("minify", function() {
  return gulp.src("public/javascripts/tag.js")
    .pipe(concat("tag.min.js"))
    .pipe(uglify())
    .pipe(gulp.dest("public/javascripts"))
});

gulp.task("build-riot", function() {
  runSequence(
    ["riot"],
    ["concat"],
    ["minify"]
  );
});

gulp.task("build-sass", function() {
  var streamSass = sass();
  var stream = gulp.src("sass/*.scss")
    .pipe(streamSass)
    .pipe(gulp.dest("public/stylesheets"))
  streamSass.on("error", function() {
    console.log("sass failed");
    stream.end();
  });
  return stream;
});

gulp.task("watch", function() {
  gulp.watch("riot/tags/*.tag", ["build-riot"]);
  gulp.watch("sass/*.scss", ["build-sass"]);
});

gulp.task("default", ["build-riot", "build-sass"]);
