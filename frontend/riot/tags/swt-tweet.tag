<swt-tweet class="sg-contents-tweet">
  <form onsubmit={onSubmit}>
    <input type="text" name="tweetUrl" oninput={onInputUrl} placeholder="気になったWEBページのアドレスを入力">
    <textarea name="tweetComment" if={isStartDisplayComment} oninput={onInputComment} class={sg-contents-tweet-comment-show: isDisplayComment} placeholder="コメントを入力"></textarea>
    <div class="sg-contents-tweet-submit">
      <span class={sg-contents-tweet-submit-invalid: commentLength > 140}><small>{commentLength}</small></span>
      <button disabled={!isDisplayComment || commentLength <= 0 || commentLength > 140}>投稿</button>
    </div>
  </form>

  <script>
    // ===================================================================================
    //                                                                             Declare
    //                                                                             =======

    declare var sawitter: any;

    // ===================================================================================
    //                                                                          Attributes
    //                                                                          ==========

    this.isStartDisplayComment = false;
    this.isDisplayComment = false;
    this.commentLength = 0;

    // ===================================================================================
    //                                                                               Event
    //                                                                               =====

    this.onInputUrl = e => {
      var url = e.target.value;
      if (url.length > 0) {
        this.isStartDisplayComment = true;
        this.update();
        setTimeout(() => {
          this.isDisplayComment = true;
          this.update();
        }, 1);
      } else {
        this.isDisplayComment = false;
        this.update();
        setTimeout(() => {
          this.isStartDisplayComment = false;
          this.update();
        }, 200);
      }
    };

    this.onInputComment = e => {
      this.commentLength = e.target.value.length;
      this.update();
    };

    this.hideComment = () => {
      this.isDisplayComment = false;
      this.update();
      setTimeout(() => {
        this.isStartDisplayComment = false;
        this.update();
      }, 200);
    };

    this.onSubmit = e => {
      e.preventDefault();
      var urlObj = this.tweetUrl;
      var commentObj = this.tweetComment;
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
        ok: () => {
          sawitter.doPost(url, comment);
          urlObj.value = "";
          commentObj.value = "";
          sawitter.obs.trigger("hideModal");
          this.commentLength = 0;
          this.hideComment();
        },
        ng: () => {
          sawitter.obs.trigger("hideModal");
        }
      });
    };

    sawitter.obs.on("onReadyPost", () => {
      this.tweetUrl.focus();
    });

    sawitter.obs.on("onPosted", () => {

    });
  </script>
</swt-tweet>
