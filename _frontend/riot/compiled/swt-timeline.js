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
