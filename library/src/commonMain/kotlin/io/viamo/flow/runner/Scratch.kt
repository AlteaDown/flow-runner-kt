package io.viamo.flow.runner

interface IParent {
  val value: Any
  val nullValue: Any?
}

interface IChildA : IParent {
  override val value: Int
  override val nullValue: Int
}

interface IChildB : IParent {
  override val value: IChildA
  override val nullValue: IChildA?
}

interface IPromptConfig2 {
  val kind: String
  val isResponseRequired: Boolean
  val prompt: String
  val value: Any?
}

interface IAdvancedSelectOnePromptConfig : IPromptConfig2 {
  val primaryField: String
  val secondaryFields: List<String>
  val choiceRowFields: List<String>
  val choiceRows: String
  val responseFields: List<String>?
  override var value: Int?
}