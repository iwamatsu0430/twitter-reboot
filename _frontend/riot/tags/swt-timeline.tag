<swt-timeline>
  <ul class="sg-contents-timeline {sg-contents-timeline-detail: isDetail}">
    <li each={tweets}>
      <dl class="sg-contents-timeline-share">
        <dt>
          <a href="/content/{shareContents.shareContentsId}" onclick={onClickDetail}>
            <img src={shareContents.thumbnailUrl} alt={shareContents.title}>
          </a>
        </dt>
        <dd>
          <h1>
            <a href="/content/{shareContents.shareContentsId}" onclick={onClickDetail}>
              {shareContents.title}
            </a>
          </h1>
        </dd>
      </dl>
      <div class="sg-contents-timeline-ismine" if={isMine}>
        <p>あなたのツイート</p>
        <button onclick={onDeleteTweet}><i class="fa fa-trash-o"></i></button>
      </div>
      <dl class="sg-contents-timeline-comment">
        <dt>
          <i class="fa fa-user fa-2x"></i>
        </dt>
        <dd>
          <p>{tweet.comment}</p>
          <time>{tweet.postedAt}</time>
        </dd>
      </dl>
      <swt-value-btns value={value} tweetid={tweet.tweetId}></swt-value-btns>
    </li>
  </ul>
  <div class="sg-contents-timeline-past">
    <button onclick={findPastTweet}>さらに20件取得</button>
  </div>

  <script>
    // ===================================================================================
    //                                                                             Declare
    //                                                                             =======

    declare var sawitter: any;
    declare var _: any;

    // ===================================================================================
    //                                                                          Attributes
    //                                                                          ==========

    this.isDetail = false;
    this.tweets = [];

    // ===================================================================================
    //                                                                               Event
    //                                                                               =====

    this.onClickDetail = e => {
      e.preventDefault();
      var commandLeftIndex = sawitter.currentKeyCodes.indexOf(91);
      var commandRightIndex = sawitter.currentKeyCodes.indexOf(93);
      if (commandLeftIndex >= 0 || commandRightIndex >= 0) {
        window.open(e.item.shareContents.url);
      }
      var shareContentsId = e.item.shareContents.shareContentsId;
      sawitter.showDetail(shareContentsId);
    }

    this.onDeleteTweet = e => {
      e.preventDefault();
      if (confirm("このツイートを削除しますか")) {
        sawitter.deletePost(e.item.tweet.tweetId);
      }
    }

    this.findPastTweet = e => {
      e.preventDefault();
      sawitter.findTimeline(this.tweets[this.tweets.length - 1].tweet.timestamp, null);
    }

    sawitter.obs.on("onLoadTimeline", timeline => {
      this.tweets = _
        .chain(this.tweets)
        .union(timeline)
        .uniq(t => {
          return t.tweet.tweetId;
        })
        .sortBy(t => {
          return t.tweet.timestamp;
        })
        .reverse()
        .value();
      this.update();
    });

    sawitter.obs.on("showDetail", () => {
      this.isDetail = true;
      this.update();
    });

    sawitter.obs.on("hideDetail", () => {
      this.isDetail = false;
      this.update();
    });

    sawitter.obs.on("onValueUpdated", valueInfo => {
      this.update();
    });

    sawitter.obs.on("onPosted", () => {
      setTimeout(callFindTimeline, 1000);
    });

    sawitter.obs.on("onDeleted", tweetId => {
      this.tweets = _
        .chain(this.tweets)
        .filter(t => {
          return t.tweet.tweetId != tweetId;
        })
        .value();
      this.update();
    });

    sawitter.obs.on("onValueUpdate", result => {
      setTimeout(() => {
        sawitter.reloadValue(result.tweetId);
      }, 1000);
    });

    sawitter.obs.on("onValueReload", result => {
      this.tweets = _
        .chain(this.tweets)
        .map(t => {
          if (t.tweet.tweetId == result.tweetId) {
            t.value = result.value;
          }
          return t;
        })
        .value();
      this.update();
    });

    // ===================================================================================
    //                                                                               Logic
    //                                                                               =====

    var callFindTimeline = () => {
      if (this.tweets.length > 0) {
        sawitter.findTimeline(null, this.tweets[0].tweet.timestamp);
      } else {
        sawitter.findTimeline();
      }
    };

    var looper = () => {
      callFindTimeline();
      setTimeout(looper, 10000);
    };

    looper();

  </script>
</swt-timeline>
