riot.tag('swt-common', '', function(opts) {// ===================================================================================
//                                                                             Declare
//                                                                             =======
var _this = this;
var API = {
    auth: {
        signUp: "/api/auth/signup",
        signIn: "/api/auth/signin",
        signOut: "/api/auth/signout"
    },
    timeline: {
        home: "/api/timeline/home"
    },
    tweet: {
        tweet: "/api/tweet/tweet",
        delete: "/api/tweet/delete/"
    },
    value: {
        count: "/api/value/count/",
        good: "/api/value/good/",
        bad: "/api/value/bad/",
        cancel: "/api/value/cancel/"
    },
    contents: "/api/contents/"
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
this.showErrorMessage = function (title, json) {
    alert(title + "\n" + json.reason);
};
this.doSignUp = function (mail, password, passwordConfirm) {
    // empty validate
    if (mail.isEmpty || password.isEmpty || passwordConfirm.isEmpty) {
        return;
    }
    // sign up
    request
        .post(API.auth.signUp)
        .send({ mail: mail, password: password, passwordConfirm: passwordConfirm })
        .set('Accept', 'application/json')
        .end(function (error, response) {
        if (response.ok) {
            var json = JSON.parse(response.text);
            // メール送信は今回カットなので、これで勘弁してください！
            console.log(json.value);
            alert("確認アドレスは[" + json.value + "]です");
            location.reload();
        }
        else {
            _this.showErrorMessage("登録に失敗しました。", JSON.parse(response.text));
        }
    });
};
this.doSignIn = function (mail, password) {
    // empty validate
    if (mail.isEmpty || password.isEmpty) {
        return;
    }
    // sign in
    request
        .post(API.auth.signIn)
        .withCredentials()
        .send({ mail: mail, password: password })
        .set('Accept', 'application/json')
        .end(function (error, response) {
        if (response.ok) {
            location.reload();
        }
        else {
            _this.showErrorMessage("サインインに失敗しました。", JSON.parse(response.text));
        }
    });
};
this.doSignOut = function () {
    request
        .post(API.auth.signOut)
        .withCredentials()
        .end(function (error, response) {
        if (response.ok) {
            location.href = "/";
        }
        else {
            alert("サインアウトに失敗しました。もうしばらく待ってから、もう一度お願いします");
        }
    });
};
this.findTimeline = function (before, after) {
    // set url parameter
    var url = API.timeline.home;
    var existsParameter = false;
    var addParameter = function (key, value) {
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
        .end(function (error, response) {
        var json = JSON.parse(response.text);
        if (response.ok) {
            _this.obs.trigger("onLoadTimeline", json.value);
        }
    });
};
this.doPost = function (url, comment) {
    request
        .post(API.tweet.tweet)
        .send({ url: url, comment: comment })
        .set('Accept', 'application/json')
        .end(function (error, response) {
        var json = JSON.parse(response.text);
        if (response.ok) {
            _this.obs.trigger("onPosted");
        }
        else {
            _this.showErrorMessage("ツイートの投稿に失敗しました。", json);
        }
    });
};
this.deletePost = function (tweetId) {
    request
        .del(API.tweet.delete + tweetId)
        .end(function (error, response) {
        var json = JSON.parse(response.text);
        if (response.ok) {
            _this.obs.trigger("onDeleted", tweetId);
        }
        else {
            _this.showErrorMessage("ツイートの削除に失敗しました。", json);
        }
    });
};
this.putGood = function (tweetId) {
    request
        .put(API.value.good + tweetId)
        .end(function (error, response) {
        var json = JSON.parse(response.text);
        if (response.ok) {
            _this.obs.trigger("onValueUpdate", { tweetId: tweetId });
        }
        else {
            _this.showErrorMessage("評価に失敗しました。", json);
        }
    });
};
this.putBad = function (tweetId) {
    request
        .put(API.value.bad + tweetId)
        .end(function (error, response) {
        var json = JSON.parse(response.text);
        if (response.ok) {
            _this.obs.trigger("onValueUpdate", { tweetId: tweetId });
        }
        else {
            _this.showErrorMessage("評価に失敗しました。", json);
        }
    });
};
this.putCancel = function (tweetId) {
    request
        .del(API.value.cancel + tweetId)
        .end(function (error, response) {
        var json = JSON.parse(response.text);
        if (response.ok) {
            _this.obs.trigger("onValueUpdate", { tweetId: tweetId });
        }
        else {
            _this.showErrorMessage("評価の取消に失敗しました。", json);
        }
    });
};
this.reloadValue = function (tweetId) {
    request
        .get(API.value.count + tweetId)
        .end(function (error, response) {
        var json = JSON.parse(response.text);
        if (response.ok) {
            _this.obs.trigger("onValueReload", { tweetId: tweetId, value: json.value });
        }
    });
};
this.findContentsDetail = function (shareContentsId) {
    request
        .get(API.contents + shareContentsId)
        .end(function (error, response) {
        if (response.ok) {
            var contents = JSON.parse(response.text).value;
            _this.obs.trigger("onContentsLoaded", contents);
        }
    });
};
this.showDetail = function (shareContentsId) {
    _this.obs.trigger("showDetail", shareContentsId);
    _this.findContentsDetail(shareContentsId);
    history.pushState(null, null, '/contents/' + shareContentsId);
};
this.generateIcon = function (input) {
    var rounds = 1;
    var size = 32;
    var outputType = "HEX";
    var hashType = "SHA-512";
    var shaObj = new jsSHA(input, "TEXT");
    var hash = shaObj.getHash(hashType, outputType, rounds);
    return new Identicon(hash, 32).toString();
};
window.addEventListener("keydown", function (e) {
    var keyCode = e.keyCode;
    var index = _this.currentKeyCodes.indexOf(keyCode);
    if (index < 0) {
        _this.currentKeyCodes.push(keyCode);
    }
});
window.addEventListener("keyup", function (e) {
    var index = _this.currentKeyCodes.indexOf(e.keyCode);
    if (index >= 0) {
        _this.currentKeyCodes.splice(index, 1);
    }
});
window.addEventListener("popstate", function (e) {
    _this.controller(e.target.location.pathname);
});
this.controller = function (path) {
    if (path == "/") {
        _this.obs.trigger("hideDetail");
    }
    else {
        var array = path.split("/");
        _this.showDetail(array[array.length - 1]);
    }
};

});
