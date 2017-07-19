package jp.iwmat.sawtter.controllers

class ApplicationController extends ControllerBase {

  def healthCheck = Action {
    Ok
  }
}
