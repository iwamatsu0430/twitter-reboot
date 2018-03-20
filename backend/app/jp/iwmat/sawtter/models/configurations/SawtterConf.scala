package jp.iwmat.sawtter.models.configurations

case class SawtterHosts(
  frontend: String,
  backend: String
)

case class SawtterConf(
  domain: String,
  hosts: SawtterHosts
)
