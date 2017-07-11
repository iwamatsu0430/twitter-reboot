<swt-common>
    <script>
    // ===================================================================================
    //                                                                             Declare
    //                                                                             =======

    declare var riot: any;
    declare var opts: any;
    declare var _: any;
    declare var Identicon: any;
    declare var jsSHA: any;
    interface Window {
      superagent: any;
      sawitter: any;
    }
    interface Event {
      originalEvent: any;
    }
    interface EventTarget {
      location: any;
    }

    var API = {
      auth: {
        signUp:   "/api/auth/signup",
        signIn:   "/api/auth/signin",
        signOut:  "/api/auth/signout"
      },
      timeline: {
        home:     "/api/timeline/home"
      },
      tweet: {
        tweet:    "/api/tweet/tweet",
        delete:   "/api/tweet/delete/"
      },
      value: {
        count:    "/api/value/count/",
        good:     "/api/value/good/",
        bad:      "/api/value/bad/",
        cancel:   "/api/value/cancel/"
      },
      contents:   "/api/contents/"
    };

    // ===================================================================================
    //                                                                          Attributes
    //                                                                          ==========

    window.sawitter = this;
    var request = window.superagent;
    this.isLogin = opts.isLogin;
    this.obs = riot.observable();
    this.currentKeyCodes = [];

    // ===================================================================================
    //                                                                               Logic
    //                                                                               =====

    this.showErrorMessage = (title, json) => {
      alert(title + "\n" + json.reason);
    }

    this.doSignUp = (mail, password, passwordConfirm) => {
      // empty validate
      if (mail.isEmpty || password.isEmpty || passwordConfirm.isEmpty) {
        return;
      }

      // sign up
      request
        .post(API.auth.signUp)
        .send({mail: mail, password: password, passwordConfirm: passwordConfirm})
        .set('Accept', 'application/json')
        .end((error, response) => {
          if (response.ok) {
            var json = JSON.parse(response.text);
            // メール送信は今回カットなので、これで勘弁してください！
            console.log(json.value);
            alert("確認アドレスは[" + json.value + "]です");
            location.reload();
          } else {
            this.showErrorMessage("登録に失敗しました。", JSON.parse(response.text));
          }
        })
    };

    this.doSignIn = (mail, password) => {
      // empty validate
      if (mail.isEmpty || password.isEmpty) {
        return;
      }

      // sign in
      request
        .post(API.auth.signIn)
        .withCredentials()
        .send({mail: mail, password: password})
        .set('Accept', 'application/json')
        .end((error, response) => {
          if (response.ok) {
            location.reload();
          } else {
            this.showErrorMessage("サインインに失敗しました。", JSON.parse(response.text));
          }
        });
    };

    this.doSignOut = () => {
      request
        .post(API.auth.signOut)
        .withCredentials()
        .end((error, response) => {
          if (response.ok) {
            location.href = "/";
          } else {
            alert("サインアウトに失敗しました。もうしばらく待ってから、もう一度お願いします");
          }
        });
    };

    this.findTimeline = (before, after) => {

      // set url parameter
      var url: string = API.timeline.home;
      var existsParameter = false;
      var addParameter = (key, value) => {
          if (!existsParameter) {
              url += "?";
              existsParameter = true;
          }
          else {
              url += "&";
          }
          url += key + "=" + value;
      };
      if (before != null) {
          addParameter("before", before);
      }
      if (after != null) {
          addParameter("after", after);
      }

      // request timeline
      request
        .get(url)
        .withCredentials()
        .end((error, response) => {
          var json = JSON.parse(response.text);
          if (response.ok) {
            this.obs.trigger("onLoadTimeline", json.value);
          }
        });
    };

    this.doPost = (url, comment) => {
      request
        .post(API.tweet.tweet)
        .send({url: url, comment: comment})
        .set('Accept', 'application/json')
        .end((error, response) => {
          var json = JSON.parse(response.text);
          if (response.ok) {
            this.obs.trigger("onPosted");
          } else {
            this.showErrorMessage("ツイートの投稿に失敗しました。", json);
          }
        });
    };

    this.deletePost = tweetId => {
      request
        .del(API.tweet.delete + tweetId)
        .end((error, response) => {
          var json = JSON.parse(response.text);
          if (response.ok) {
            this.obs.trigger("onDeleted", tweetId);
          } else {
            this.showErrorMessage("ツイートの削除に失敗しました。", json);
          }
        });
    };

    this.putGood = tweetId => {
      request
        .put(API.value.good + tweetId)
        .end((error, response) => {
          var json = JSON.parse(response.text);
          if (response.ok) {
            this.obs.trigger("onValueUpdate", {tweetId: tweetId});
          } else {
            this.showErrorMessage("評価に失敗しました。", json);
          }
        });
    };

    this.putBad = tweetId => {
      request
        .put(API.value.bad + tweetId)
        .end((error, response) => {
          var json = JSON.parse(response.text);
          if (response.ok) {
            this.obs.trigger("onValueUpdate", {tweetId: tweetId});
          } else {
            this.showErrorMessage("評価に失敗しました。", json);
          }
        });
    };

    this.putCancel = tweetId => {
      request
        .del(API.value.cancel + tweetId)
        .end((error, response) => {
          var json = JSON.parse(response.text)
          if (response.ok) {
            this.obs.trigger("onValueUpdate", {tweetId: tweetId});
          } else {
            this.showErrorMessage("評価の取消に失敗しました。", json);
          }
        })
    };

    this.reloadValue = tweetId => {
      request
        .get(API.value.count + tweetId)
        .end((error, response) => {
          var json = JSON.parse(response.text)
          if (response.ok) {
            this.obs.trigger("onValueReload", {tweetId: tweetId, value: json.value});
          }
        });
    };

    this.findContentsDetail = shareContentsId => {
      request
        .get(API.contents + shareContentsId)
        .end((error, response) => {
          if (response.ok) {
            var contents = JSON.parse(response.text).value;
            this.obs.trigger("onContentsLoaded", contents);
          }
        });
    };

    this.showDetail = shareContentsId => {
      this.obs.trigger("showDetail", shareContentsId);
      this.findContentsDetail(shareContentsId);
      history.pushState(null, null, '/contents/' + shareContentsId);
    };

    this.generateIcon = input => {
      var rounds = 1;
      var size = 32;
      var outputType = "HEX";
      var hashType = "SHA-512";
      var shaObj = new jsSHA(input, "TEXT");
      var hash = shaObj.getHash(hashType, outputType, rounds);
      return new Identicon(hash, 32).toString();
    };

    window.addEventListener("keydown", e => {
      var keyCode = e.keyCode;
      var index = this.currentKeyCodes.indexOf(keyCode);
      if (index < 0) {
        this.currentKeyCodes.push(keyCode);
      }
    });

    window.addEventListener("keyup", e => {
      var index = this.currentKeyCodes.indexOf(e.keyCode);
      if (index >= 0) {
        this.currentKeyCodes.splice(index, 1);
      }
    });

    window.addEventListener("popstate", e => {
      this.controller(e.target.location.pathname);
    });

    this.controller = (path: string) => {
      if (path == "/") {
        this.obs.trigger("hideDetail");
      } else {
        var array: string[] = path.split("/");
        this.showDetail(array[array.length - 1]);
      }
    }

  </script>
</swt-common>
