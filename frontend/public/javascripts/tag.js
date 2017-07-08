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

riot.tag('swt-contents', '<div class="sg-contents {sg-contents-separate: isDetail}"><swt-cover if="{!isDetail && !sawitter.isLogin}"></swt-cover><swt-tweet if="{!isDetail && sawitter.isLogin}"></swt-tweet><swt-timeline if="{!isDetail}"></swt-timeline><swt-detail if="{isDetail}"></swt-detail><swt-iframe if="{isDetail}"></swt-iframe><swt-modal if="{isShowModal}"></swt-modal></div>', function(opts) {// ===================================================================================
//                                                                             Declare
//                                                                             =======
var _this = this;
// ===================================================================================
//                                                                          Attributes
//                                                                          ==========
this.isDetail = false;
this.isShowModal = false;
// ===================================================================================
//                                                                               Event
//                                                                               =====
sawitter.obs.on("showDetail", function () {
    _this.isDetail = true;
    _this.update();
});
sawitter.obs.on("hideDetail", function () {
    _this.isDetail = false;
    _this.update();
});
sawitter.obs.on("showModal", function () {
    _this.isShowModal = true;
    _this.update();
});
sawitter.obs.on("hideModal", function () {
    setTimeout(function () {
        _this.isShowModal = false;
        _this.update();
    }, 300);
});

});

riot.tag('swt-cover', '<div class="sg-contents-cover"><h1>Sawitter</h1><p>匿名！コメント！楽しい！</p></div>', function(opts) {
});

riot.tag('swt-detail', '<div class="sg-contents-detail"><section><header><h1><a href="{contents.shareContents.url}" target="_blank">{contents.shareContents.title}</a></h1><p>{contents.shareContents.url}</p></header><swt-tweet-comment if="{sawitter.isLogin}" url="{contents.shareContents.url}"></swt-tweet-comment><ul class="sg-contents-timeline-sort"><li><button onclick="{sortByNew}" class="{sg-contents-timeline-sort-active: sortMode == 0}">新着順</button></li><li><button onclick="{sortByGood}" class="{sg-contents-timeline-sort-active: sortMode == 1}">Good順</button></li><li><button onclick="{sortByBad}" class="{sg-contents-timeline-sort-active: sortMode == 2}">Bad順</button></li></ul><ul class="sg-contents-timeline"><li each="{contents.tweets}"><section><dl class="sg-contents-timeline-comment"><dt><img alt="icon" riot-src="data:image/png;base64,{generateIcon(identityHash)}"></dt><dd><p>{tweet.comment}</p><time>{tweet.postedAt}</time></dd></dl><div class="sg-contents-timeline-ismine sg-contents-timeline-ismine-detail" if="{isMine}"><p>あなたのツイート</p><button onclick="{onDeleteTweet}"><i class="fa fa-trash-o"></i></button></div><swt-value-btns value="{value}" tweetid="{tweet.tweetId}"></swt-value-btns></section></li></ul></section></div>', function(opts) {// ===================================================================================
//                                                                             Declare
//                                                                             =======
var _this = this;
var SortMode;
(function (SortMode) {
    SortMode[SortMode["new"] = 0] = "new";
    SortMode[SortMode["good"] = 1] = "good";
    SortMode[SortMode["bad"] = 2] = "bad";
})(SortMode || (SortMode = {}));
// ===================================================================================
//                                                                          Attributes
//                                                                          ==========
this.contents = {};
this.sortMode = SortMode.new;
// ===================================================================================
//                                                                               Event
//                                                                               =====
this.onDeleteTweet = function (e) {
    e.preventDefault();
    if (confirm("このツイートを削除しますか")) {
        sawitter.deletePost(e.item.tweet.tweetId);
    }
};
this.sortByNew = function (e) {
    e.preventDefault();
    _this.sortMode = SortMode.new;
    _this.sort();
};
this.sortByGood = function (e) {
    e.preventDefault();
    _this.sortMode = SortMode.good;
    _this.sort();
};
this.sortByBad = function (e) {
    e.preventDefault();
    _this.sortMode = SortMode.bad;
    _this.sort();
};
sawitter.obs.on("onContentsLoaded", function (contents) {
    _this.contents = contents;
    _this.sort();
    _this.update();
});
sawitter.obs.on("onPosted", function () {
    if (_this.contents.shareContents != undefined) {
        setTimeout(function () {
            sawitter.findContentsDetail(_this.contents.shareContents.shareContentsId);
        }, 1000);
    }
});
sawitter.obs.on("onDeleted", function (tweetId) {
    _this.contents.tweets = _
        .chain(_this.contents.tweets)
        .filter(function (c) {
        return c.tweet.tweetId != tweetId;
    })
        .value();
    _this.update();
});
sawitter.obs.on("onValueUpdate", function (result) {
    setTimeout(function () {
        sawitter.reloadValue(result.tweetId);
    }, 1000);
});
sawitter.obs.on("onValueReload", function (result) {
    _this.contents.tweets = _
        .chain(_this.contents.tweets)
        .map(function (t) {
        if (t.tweet.tweetId == result.tweetId) {
            t.value = result.value;
        }
        return t;
    })
        .value();
    _this.update();
});
this.generateIcon = function (hash) {
    var source = sawitter.generateIcon(hash);
    return source;
};
// ===================================================================================
//                                                                               Logic
//                                                                               =====
this.sort = function () {
    _this.contents.tweets = _
        .chain(_this.contents.tweets)
        .sortBy(function (tweet) {
        switch (_this.sortMode) {
            case SortMode.new:
                return tweet.tweet.timestamp;
            case SortMode.good:
                return tweet.value.good;
            case SortMode.bad:
                return tweet.value.bad;
        }
    })
        .reverse()
        .value();
    _this.update();
};

});

riot.tag('swt-footer', '<footer class="sg-footer"><div class="sg-container"><p>(c)2015 SAW</p></div></footer>', function(opts) {
});

riot.tag('swt-header', '<header class="sg-header"><h1 class="sg-header-logo"><a href="/"><i class="fa fa-user-secret fa"></i> Sawitter</a></h1><ul><li if="{sawitter.isLogin}" class="sg-header-tweet"><a href="#" onclick="{tweetNews}"><i class="fa fa-pencil-square-o"></i></a></li><li class="sg-header-signs"><button if="{!sawitter.isLogin}" onclick="{onSignin}" class="sg-header-signin">サインイン</button><button if="{!sawitter.isLogin}" onclick="{onSignup}" class="sg-header-signup">登録</button><button if="{sawitter.isLogin}" onclick="{onSignout}" class="sg-header-signout">サインアウト</button></li></ul></header><form name="signin" class="sg-header-signs-signin" if="{false}"><label>メールアドレス</label><input type="text" name="signinMail" placeholder="メールアドレス"><label>パスワード(6~32桁の英数小大文字)</label><input type="password" name="signinPassword" placeholder="6~32桁の英数小大文字"></form><form name="signup" class="sg-header-signs-signup" if="{false}"><label>メールアドレス</label><input type="text" name="signupMail" placeholder="メールアドレス"><label>パスワード</label><input type="password" name="signupPassword" placeholder="6~32桁の英数小大文字"><label>パスワード(再確認)</label><input type="password" name="signupPasswordConfirm" placeholder="6~32桁の英数小大文字"></form>', function(opts) {// ===================================================================================
//                                                                             Declare
//                                                                             =======
var _this = this;
// ===================================================================================
//                                                                               Event
//                                                                               =====
this.tweetNews = function (e) {
    e.preventDefault();
    var runTime = 10;
    var fps = 60;
    var diffY = window.scrollY / fps;
    var scrollToTop = function () {
        var currentY = window.scrollY;
        var targetY = currentY - diffY;
        var targetY = targetY <= 0 ? 0 : targetY;
        if (currentY > 0) {
            window.scrollTo(0, targetY);
            if (targetY > 0) {
                setTimeout(scrollToTop, runTime / fps);
            }
            else {
                sawitter.obs.trigger("onReadyPost");
            }
        }
        else {
            sawitter.obs.trigger("onReadyPost");
        }
    };
    scrollToTop();
};
this.onSignin = function (e) {
    e.preventDefault();
    sawitter.obs.trigger("showModal", {
        title: "サインイン",
        raw: _this.signin.innerHTML,
        okButtonMsg: "サインイン",
        ngButtonMsg: "キャンセル",
        ok: function (raw) {
            var mail = raw.querySelector('input[name="signinMail"]').value.trim();
            var password = raw.querySelector('input[name="signinPassword"]').value.trim();
            if (mail == "") {
                alert("メールアドレスが入力されていません");
                return;
            }
            if (password == "") {
                alert("パスワードが入力されていません");
                return;
            }
            sawitter.doSignIn(mail, password);
        },
        ng: function (raw) {
            sawitter.obs.trigger("hideModal");
        }
    });
};
this.onSignup = function (e) {
    e.preventDefault();
    sawitter.obs.trigger("showModal", {
        title: "登録",
        raw: _this.signup.innerHTML,
        okButtonMsg: "登録",
        ngButtonMsg: "キャンセル",
        ok: function (raw) {
            var mail = raw.querySelector('input[name="signupMail"]').value.trim();
            var password = raw.querySelector('input[name="signupPassword"]').value.trim();
            var passwordConfirm = raw.querySelector('input[name="signupPasswordConfirm"]').value.trim();
            if (mail == "") {
                alert("メールアドレスが入力されていません");
                return;
            }
            if (password == "") {
                alert("パスワードが入力されていません");
                return;
            }
            if (passwordConfirm == "") {
                alert("パスワード(再確認)が入力されていません");
                return;
            }
            sawitter.doSignUp(mail, password, passwordConfirm);
        },
        ng: function (raw) {
            sawitter.obs.trigger("hideModal");
        }
    });
};
this.onSignout = function (e) {
    e.preventDefault();
    sawitter.doSignOut();
};

});

riot.tag('swt-iframe', '<iframe class="sg-contents-iframe" name="contentsIframe"></iframe>', function(opts) {// ===================================================================================
//                                                                             Declare
//                                                                             =======
var _this = this;
// ===================================================================================
//                                                                               Event
//                                                                               =====
sawitter.obs.on("onContentsLoaded", function (contents) {
    _this.contentsIframe.src = contents.shareContents.url;
});

});

riot.tag('swt-modal', '<div class="sg-contents-modal"><div class="{sg-contents-modal-bg: isShowModal}" onclick="{closeModal}"></div><div if="{isShowModal}" class="sg-contents-modal-contents"><section><header class="sg-contents-modal-contents-header"><h1>{contents.title}</h1></header><div if="{contents.raw != null}" name="raw" class="sg-contents-modal-contents-raw"></div><div if="{contents.msg != null}">{contents.msg}</div><div if="{contents.msgSub != null}" class="sg-contents-modal-contents-msg-sub">{contents.msgSub}</div><footer class="sg-contents-modal-contents-footer"><ul><li><button onclick="{onOk}" class="sg-contents-modal-btn-ok">{contents.okButtonMsg}</button></li><li><button onclick="{onNg}" class="sg-contents-modal-btn-ng">{contents.ngButtonMsg}</button></li></ul></footer></section></div></div>', function(opts) {// ===================================================================================
//                                                                             Declare
//                                                                             =======
var _this = this;
// ===================================================================================
//                                                                          Attributes
//                                                                          ==========
this.isShowModal = false;
this.contents = {};
// ===================================================================================
//                                                                               Event
//                                                                               =====
this.closeModal = function (e) {
    e.preventDefault();
    sawitter.obs.trigger("hideModal");
};
this.onOk = function (e) {
    e.preventDefault();
    _this.contents.ok(_this.raw);
};
this.onNg = function (e) {
    e.preventDefault();
    _this.contents.ng(_this.raw);
};
sawitter.obs.on("showModal", function (contents) {
    _this.contents = contents;
    _this.raw.innerHTML = contents.raw;
    setTimeout(function () {
        _this.isShowModal = true;
        _this.update();
    }, 1);
});
sawitter.obs.on("hideModal", function () {
    _this.contents = {};
    _this.raw.innerHTML = "";
    _this.isShowModal = false;
    _this.update();
});

});

riot.tag('swt-timeline', '<ul class="sg-contents-timeline {sg-contents-timeline-detail: isDetail}"><li each="{tweets}"><dl class="sg-contents-timeline-share"><dt><a href="/content/{shareContents.shareContentsId}" onclick="{onClickDetail}"><img riot-src="{shareContents.thumbnailUrl}" alt="{shareContents.title}"></a></dt><dd><h1><a href="/content/{shareContents.shareContentsId}" onclick="{onClickDetail}"> {shareContents.title} </a></h1></dd></dl><div class="sg-contents-timeline-ismine" if="{isMine}"><p>あなたのツイート</p><button onclick="{onDeleteTweet}"><i class="fa fa-trash-o"></i></button></div><dl class="sg-contents-timeline-comment"><dt><i class="fa fa-user fa-2x"></i></dt><dd><p>{tweet.comment}</p><time>{tweet.postedAt}</time></dd></dl><swt-value-btns value="{value}" tweetid="{tweet.tweetId}"></swt-value-btns></li></ul><div class="sg-contents-timeline-past"><button onclick="{findPastTweet}">さらに20件取得</button></div>', function(opts) {// ===================================================================================
//                                                                             Declare
//                                                                             =======
var _this = this;
// ===================================================================================
//                                                                          Attributes
//                                                                          ==========
this.isDetail = false;
this.tweets = [];
// ===================================================================================
//                                                                               Event
//                                                                               =====
this.onClickDetail = function (e) {
    e.preventDefault();
    var commandLeftIndex = sawitter.currentKeyCodes.indexOf(91);
    var commandRightIndex = sawitter.currentKeyCodes.indexOf(93);
    if (commandLeftIndex >= 0 || commandRightIndex >= 0) {
        window.open(e.item.shareContents.url);
    }
    var shareContentsId = e.item.shareContents.shareContentsId;
    sawitter.showDetail(shareContentsId);
};
this.onDeleteTweet = function (e) {
    e.preventDefault();
    if (confirm("このツイートを削除しますか")) {
        sawitter.deletePost(e.item.tweet.tweetId);
    }
};
this.findPastTweet = function (e) {
    e.preventDefault();
    sawitter.findTimeline(_this.tweets[_this.tweets.length - 1].tweet.timestamp, null);
};
sawitter.obs.on("onLoadTimeline", function (timeline) {
    _this.tweets = _
        .chain(_this.tweets)
        .union(timeline)
        .uniq(function (t) {
        return t.tweet.tweetId;
    })
        .sortBy(function (t) {
        return t.tweet.timestamp;
    })
        .reverse()
        .value();
    _this.update();
});
sawitter.obs.on("showDetail", function () {
    _this.isDetail = true;
    _this.update();
});
sawitter.obs.on("hideDetail", function () {
    _this.isDetail = false;
    _this.update();
});
sawitter.obs.on("onValueUpdated", function (valueInfo) {
    _this.update();
});
sawitter.obs.on("onPosted", function () {
    setTimeout(callFindTimeline, 1000);
});
sawitter.obs.on("onDeleted", function (tweetId) {
    _this.tweets = _
        .chain(_this.tweets)
        .filter(function (t) {
        return t.tweet.tweetId != tweetId;
    })
        .value();
    _this.update();
});
sawitter.obs.on("onValueUpdate", function (result) {
    setTimeout(function () {
        sawitter.reloadValue(result.tweetId);
    }, 1000);
});
sawitter.obs.on("onValueReload", function (result) {
    _this.tweets = _
        .chain(_this.tweets)
        .map(function (t) {
        if (t.tweet.tweetId == result.tweetId) {
            t.value = result.value;
        }
        return t;
    })
        .value();
    _this.update();
});
// ===================================================================================
//                                                                               Logic
//                                                                               =====
var callFindTimeline = function () {
    if (_this.tweets.length > 0) {
        sawitter.findTimeline(null, _this.tweets[0].tweet.timestamp);
    }
    else {
        sawitter.findTimeline();
    }
};
var looper = function () {
    callFindTimeline();
    setTimeout(looper, 10000);
};
looper();

});

riot.tag('swt-tweet-comment', '<div class="sg-contents-tweet"><form onsubmit="{onSubmit}"><input type="hidden" value="{opts.url}" name="tweetUrl" ><textarea name="tweetComment" oninput="{onInputComment}" class="sg-contents-tweet-comment-show" placeholder="コメントを入力"></textarea><div class="sg-contents-tweet-submit"><span class="{sg-contents-tweet-submit-invalid: commentLength > 140}"><small>{commentLength}</small></span><button __disabled="{commentLength <= 0 || commentLength > 140}">投稿</button></div></form></div>', function(opts) {// ===================================================================================
//                                                                             Declare
//                                                                             =======
var _this = this;
// ===================================================================================
//                                                                          Attributes
//                                                                          ==========
this.commentLength = 0;
// ===================================================================================
//                                                                               Event
//                                                                               =====
this.onInputComment = function (e) {
    _this.commentLength = e.target.value.length;
    _this.update();
};
this.onSubmit = function (e) {
    e.preventDefault();
    var urlObj = _this.tweetUrl;
    var commentObj = _this.tweetComment;
    var url = urlObj.value.trim();
    var comment = commentObj.value.trim();
    if (url == "") {
        alert("URLを入力してください");
        return;
    }
    if (comment == "") {
        alert("コメントを入力してください");
        return;
    }
    sawitter.obs.trigger("showModal", {
        title: "投稿確認",
        msg: comment,
        msgSub: "WEBページ(" + url + ")について、このコメントを投稿してもよろしいでしょうか？",
        okButtonMsg: "投稿",
        ngButtonMsg: "キャンセル",
        ok: function () {
            sawitter.doPost(url, comment);
            urlObj.value = "";
            commentObj.value = "";
            _this.commentLength = 0;
            sawitter.obs.trigger("hideModal");
        },
        ng: function () {
            sawitter.obs.trigger("hideModal");
        }
    });
};
sawitter.obs.on("onReadyPost", function () {
    _this.tweetComment.focus();
});

});

riot.tag('swt-tweet', '<form onsubmit="{onSubmit}"><input type="text" name="tweetUrl" oninput="{onInputUrl}" placeholder="気になったWEBページのアドレスを入力"><textarea name="tweetComment" if="{isStartDisplayComment}" oninput="{onInputComment}" class="{sg-contents-tweet-comment-show: isDisplayComment}" placeholder="コメントを入力"></textarea><div class="sg-contents-tweet-submit"><span class="{sg-contents-tweet-submit-invalid: commentLength > 140}"><small>{commentLength}</small></span><button __disabled="{!isDisplayComment || commentLength <= 0 || commentLength > 140}">投稿</button></div></form>', 'class="sg-contents-tweet"', function(opts) {// ===================================================================================
//                                                                             Declare
//                                                                             =======
var _this = this;
// ===================================================================================
//                                                                          Attributes
//                                                                          ==========
this.isStartDisplayComment = false;
this.isDisplayComment = false;
this.commentLength = 0;
// ===================================================================================
//                                                                               Event
//                                                                               =====
this.onInputUrl = function (e) {
    var url = e.target.value;
    if (url.length > 0) {
        _this.isStartDisplayComment = true;
        _this.update();
        setTimeout(function () {
            _this.isDisplayComment = true;
            _this.update();
        }, 1);
    }
    else {
        _this.isDisplayComment = false;
        _this.update();
        setTimeout(function () {
            _this.isStartDisplayComment = false;
            _this.update();
        }, 200);
    }
};
this.onInputComment = function (e) {
    _this.commentLength = e.target.value.length;
    _this.update();
};
this.hideComment = function () {
    _this.isDisplayComment = false;
    _this.update();
    setTimeout(function () {
        _this.isStartDisplayComment = false;
        _this.update();
    }, 200);
};
this.onSubmit = function (e) {
    e.preventDefault();
    var urlObj = _this.tweetUrl;
    var commentObj = _this.tweetComment;
    var url = urlObj.value.trim();
    var comment = commentObj.value.trim();
    if (url == "") {
        alert("URLを入力してください");
        return;
    }
    if (comment == "") {
        alert("コメントを入力してください");
        return;
    }
    sawitter.obs.trigger("showModal", {
        title: "投稿確認",
        msg: comment,
        msgSub: "WEBページ(" + url + ")について、このコメントを投稿してもよろしいでしょうか？",
        okButtonMsg: "投稿",
        ngButtonMsg: "キャンセル",
        ok: function () {
            sawitter.doPost(url, comment);
            urlObj.value = "";
            commentObj.value = "";
            sawitter.obs.trigger("hideModal");
            _this.commentLength = 0;
            _this.hideComment();
        },
        ng: function () {
            sawitter.obs.trigger("hideModal");
        }
    });
};
sawitter.obs.on("onReadyPost", function () {
    _this.tweetUrl.focus();
});
sawitter.obs.on("onPosted", function () {
});

});

riot.tag('swt-value-btns', '<div class="sg-contents-timeline-btn"><div if="{sawitter.isLogin && !opts.value.isValued}"><a class="sg-contents-timeline-btn-good" onclick="{onPutGood}" href="#"><i class="fa fa-thumbs-up"></i> {opts.value.good} <span><i class="fa fa-thumbs-up"></i> Good </span></a></div><div if="{sawitter.isLogin && !opts.value.isValued}"><a class="sg-contents-timeline-btn-bad" onclick="{onPutBad}" href="#"><i class="fa fa-thumbs-down"></i> {opts.value.bad} <span><i class="fa fa-thumbs-down"></i> Bad </span></a></div><div if="{sawitter.isLogin && opts.value.isValued}"><a class="sg-contents-timeline-btn-good sg-contents-timeline-btn-complete" onclick="{onCancel}" href="#"><i class="fa fa-thumbs-up"></i> {opts.value.good} <span>Cancel</span></a></div><div if="{sawitter.isLogin && opts.value.isValued}"><a class="sg-contents-timeline-btn-bad sg-contents-timeline-btn-complete" onclick="{onCancel}" href="#"><i class="fa fa-thumbs-down"></i> {opts.value.bad} <span>Cancel</span></a></div><div if="{!sawitter.isLogin}"><a class="sg-contents-timeline-btn-good"><i class="fa fa-thumbs-up"></i> {opts.value.good} </a></div><div if="{!sawitter.isLogin}"><a class="sg-contents-timeline-btn-bad"><i class="fa fa-thumbs-down"></i> {opts.value.bad} </a></div></div>', function(opts) {// ===================================================================================
//                                                                             Declare
//                                                                             =======
// ===================================================================================
//                                                                               Event
//                                                                               =====
this.onPutGood = function (e) {
    e.preventDefault();
    sawitter.putGood(opts.tweetid);
};
this.onPutBad = function (e) {
    e.preventDefault();
    sawitter.putBad(opts.tweetid);
};
this.onCancel = function (e) {
    e.preventDefault();
    var result = confirm("評価を取り消しますか？");
    if (result) {
        sawitter.putCancel(opts.tweetid);
    }
};

});
