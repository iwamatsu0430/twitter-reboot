<swt-detail>
  <div class="sg-contents-detail">
    <section>
      <header>
        <h1><a href="{contents.shareContents.url}" target="_blank">{contents.shareContents.title}</a></h1>
        <p>{contents.shareContents.url}</p>
      </header>
      <swt-tweet-comment if={sawitter.isLogin} url={contents.shareContents.url}></swt-tweet-comment>
      <ul class="sg-contents-timeline-sort">
        <li>
          <button onclick={sortByNew} class={sg-contents-timeline-sort-active: sortMode == 0}>新着順</button>
        </li>
        <li>
          <button onclick={sortByGood} class={sg-contents-timeline-sort-active: sortMode == 1}>Good順</button>
        </li>
        <li>
          <button onclick={sortByBad} class={sg-contents-timeline-sort-active: sortMode == 2}>Bad順</button>
        </li>
      </ul>
      <ul class="sg-contents-timeline">
        <li each={contents.tweets}>
          <section>
            <dl class="sg-contents-timeline-comment">
              <dt>
                <img alt="icon" src="data:image/png;base64,{generateIcon(identityHash)}">
              </dt>
              <dd>
                <p>{tweet.comment}</p>
                <time>{tweet.postedAt}</time>
              </dd>
            </dl>
            <div class="sg-contents-timeline-ismine sg-contents-timeline-ismine-detail" if={isMine}>
              <p>あなたのツイート</p>
              <button onclick={onDeleteTweet}><i class="fa fa-trash-o"></i></button>
            </div>
            <swt-value-btns value={value} tweetid={tweet.tweetId}></swt-value-btns>
          </section>
        </li>
      </ul>
    </section>
  </div>

  <script>
    // ===================================================================================
    //                                                                             Declare
    //                                                                             =======

    declare var sawitter: any;
    declare var _: any;

    enum SortMode {
      new,
      good,
      bad
    }

    // ===================================================================================
    //                                                                          Attributes
    //                                                                          ==========

    this.contents = {};
    this.sortMode = SortMode.new;

    // ===================================================================================
    //                                                                               Event
    //                                                                               =====

    this.onDeleteTweet = e => {
      e.preventDefault();
      if (confirm("このツイートを削除しますか")) {
        sawitter.deletePost(e.item.tweet.tweetId);
      }
    }

    this.sortByNew = e => {
      e.preventDefault();
      this.sortMode = SortMode.new;
      this.sort();
    };

    this.sortByGood = e => {
      e.preventDefault();
      this.sortMode = SortMode.good;
      this.sort();
    };

    this.sortByBad = e => {
      e.preventDefault();
      this.sortMode = SortMode.bad;
      this.sort();
    };

    sawitter.obs.on("onContentsLoaded", contents => {
      this.contents = contents;
      this.sort();
      this.update();
    });

    sawitter.obs.on("onPosted", () => {
      if (this.contents.shareContents != undefined) {
        setTimeout(() => {
          sawitter.findContentsDetail(this.contents.shareContents.shareContentsId);
        }, 1000);
      }
    });

    sawitter.obs.on("onDeleted", tweetId => {
      this.contents.tweets = _
        .chain(this.contents.tweets)
        .filter(c => {
          return c.tweet.tweetId != tweetId;
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
      this.contents.tweets = _
        .chain(this.contents.tweets)
        .map(t => {
          if (t.tweet.tweetId == result.tweetId) {
            t.value = result.value;
          }
          return t;
        })
        .value();
      this.update();
    });

    this.generateIcon = hash => {
      var source = sawitter.generateIcon(hash);
      return source;
    }

    // ===================================================================================
    //                                                                               Logic
    //                                                                               =====

    this.sort = () => {
      this.contents.tweets = _
      .chain(this.contents.tweets)
      .sortBy(tweet => {
        switch (this.sortMode) {
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
      this.update();
    };

  </script>
</swt-detail>
