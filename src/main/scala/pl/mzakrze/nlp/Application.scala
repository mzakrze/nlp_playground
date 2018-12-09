package pl.mzakrze.nlp

import opennlp.tools.namefind.TokenNameFinderModel
import opennlp.tools.tokenize.TokenizerME
import opennlp.tools.tokenize.TokenizerModel
import opennlp.tools.namefind.NameFinderME
import java.io.{BufferedInputStream, FileInputStream, IOException}

import opennlp.tools.util.Span

object Application {

  val text = scala.io.Source.fromInputStream(new FileInputStream("database/paragraphs/politics.txt")).mkString.replace("\n", " ")

  val personModelFile = "database/en-ner-person.bin"
  val tokenModelFile = "database/en-token.bin"

  def main(args: Array[String]) = {

    println("Loading model ...")

    val bis = new BufferedInputStream(new FileInputStream(personModelFile))

    val model = new TokenNameFinderModel(bis)

    println("Model loaded. Tokenizing ...")

    val nameFinder = new NameFinderME(model)

    val tokens = tokenize(text)

    val nameSpans = nameFinder.find(tokens)

    debug(nameSpans, tokens.toList)
  }

  def debug(spans: Iterable[Span], tokens: List[String]) = {
    for(ns <- spans){
      val substring = tokens.slice(ns.getStart, ns.getEnd).mkString(" ")
      val prob = ns.getProb
      val entityType = ns.getType
      println("{" + substring + "} -> " + entityType + "(" + prob + ")")
    }
  }

  @throws[IOException]
  def tokenize(sentence: String): Array[String] = {
    val bis = new BufferedInputStream(new FileInputStream(tokenModelFile))
    val tokenModel = new TokenizerModel(bis)
    val tokenizer = new TokenizerME(tokenModel)
    tokenizer.tokenize(sentence)
  }

}
