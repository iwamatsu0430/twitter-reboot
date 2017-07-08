package utils

import com.sksamuel.elastic4s.ElasticClient
import org.elasticsearch.common.settings.ImmutableSettings
import play.api.{Mode, Logger, Application}

/**
 * @author SAW
 */
object ElasticsearchUtil {
  lazy val logger = Logger(this.getClass)

  private[this] var client: Option[ElasticClient] = None


  /**
   * 処理用
   */
  def process[A](f: ElasticClient => A): A =
    client.map(f).getOrElse{
      throw new IllegalStateException("Elasticsearch client is not initialized.")
    }


  /**
   * 初期化
   */
  def init(app: Application):Unit = {
    def additionalConfig(): Option[Seq[(String, String)]] =
      app.configuration.getConfig("elasticsearch.extras").map{ ex =>
        ex.keys.flatMap{ sk =>
          ex.getString(sk).map(v => sk -> v)
        }.toSeq
      }


    val configs = (for {
      host <- app.configuration.getString("elasticsearch.host")
      port <- app.configuration.getInt("elasticsearch.port")
      extras <- additionalConfig()
    } yield {
        Some((host, port, extras))
      }).getOrElse{
      if (app.mode != Mode.Dev)
        throw new RuntimeException("Elasticsearch configuration doesn't exist")
      else {
        logger.info("Elasticsearch initialization was skipped because the configuration doesn't exist.")
        None
      }
    }

    client = configs.map{ case (host, port, extras) => remote(host, port, extras:_*)}
  }


  /**
   * elastic4s ElasticClient クライアントの初期化
   */
  private[this] def remote(host: String, port: Int, settings: (String, String)*): ElasticClient = {
    val built = settings
      .foldLeft(ImmutableSettings.settingsBuilder()) { case (builder, (k, v)) =>
      builder.put(k, v)
    }.build()

    ElasticClient.remote(built, host, port)
  }
}