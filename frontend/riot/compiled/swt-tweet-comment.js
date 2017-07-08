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
