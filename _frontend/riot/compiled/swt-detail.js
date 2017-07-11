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
