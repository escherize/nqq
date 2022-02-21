(ns nqq.core
  (:require ["fs" :as fs]
            ["enquirer$default" :as enquirer]
            [clojure.edn :as edn]))

(defn usages []
  (println "nqq questions out-file(optional)")
  (println "")
  (println "Ask questions from a declarative script.")
  (println "")
  (println "Arguments:")
  (println "  - questions")
  (println "    edn string representing a single or collection of maps which get processed by enquire.js as prompts.")
  (println "  - out-file (optional)")
  (println "    [optional, answers.edn by default]")
  (println)
  (println "References for enquirer js:")
  (println "  - for the enquire.js prompt types:")
  (println "    https://github.com/enquirer/enquirer#-built-in-prompts")
  (println "  - the names of `type`s:")
  (println "    https://github.com/enquirer/enquirer/blob/8d626c206733420637660ac7c2098d7de45e8590/lib/prompts/index.js")
  (println "")
  (println "Examples:")
  (println "  1.")
  (println "    Run:")
  (println "      nqq '{:type :input :name :toy :message \"Favorite toy?\" :initial \"top\"}' && cat answers.edn")
  (println "    Then hit return and nqq will print: {:toy \"top\"}")
  (println "")
  (.exit js/process 1))

(defn kw-keys [m]
  (zipmap (mapv keyword (keys m)) (vals m)))



(defn -main [& args]
  (let [;; TODO get together a malli schema for questions
        [questions out-file] args
        _ (when-not questions (usages))
        out-file (or out-file "answers.edn")
        json-questions (clj->js (edn/read-string (first args)))]
    (->
      enquirer
      (.prompt json-questions)
      (.then (fn [answers]
               (def value (pr-str (kw-keys (js->clj answers))))))
      (.catch (fn [_] (.writeFile fs out-file value (fn [_]))))
      (.then (fn [_] (.writeFile fs out-file value (fn [_])))))))
