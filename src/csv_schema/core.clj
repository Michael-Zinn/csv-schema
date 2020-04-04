(ns csv-schema.core
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]
            [clojure.string :as string]
            [clojure.data.csv]))

;; Parsers

(defn- create-localdate-parser
  "For example, 22.09.1984 would be dd.MM.yyyy"
  [pattern]
  (fn [v] (java.time.LocalDate/parse v (java.time.format.DateTimeFormatter/ofPattern pattern))))

(defn- create-bigdecimal-parser
  "This parsers allows for thousands separator and decimal separator."
  [decimal-separator thousands-separator]
  (fn [v] (-> v
              (string/replace thousands-separator "")
              (string/replace decimal-separator ".")
              bigdec)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn- create-column-parser
  "Creates a parser for values of the column.
  Returns the identity function if no parser is
  specified"
  [column]
  ; (if (string? column)
  identity)
; (let [[name [type tparam1 tparam2]] column]
; (println "36" name type tparam1 tparam2)
; (case type
; :LocalDate (create-localdate-parser (or tparam1 "yyyy-MM-dd"))
; :BigDecimal (create-bigdecimal-parser (or tparam1 ".") (or tparam2 ""))
; identity ; FIXME hack
; )
; )
; )
; )

(defn- get-column-name
  [column]
  (if (string? column)
    column
    (first column)))

(defn- column-names
  [format]
  (map get-column-name (format :columns)))

(defn- create-parsers
  "Returns a seq of parsers, corresponding to the columns."
  [format]
  (map create-column-parser (format :columns)))

(def csv-example
  {:path     "/home/michael/doc/kontoauszug2.csv"
   :encoding "cp1250"})



(defn- parse-csv-row
  "Parses a CSV row according to the given format"
  [format parsers row]
  (println format parsers row)
  (->
    row
    ; (string/split (re-pattern (format :terminator)))
    ; (->> (map remove-first-and-last))
    (->> (map (fn [f c] (f c)) parsers))
    )
  )

(defn- parse-csv
  "Parse csv (in a string) according to the given format"
  [format csv]
  (let
    [column-names (map get-column-name (format :columns))
     parsers (create-parsers format)
     rows-raw (clojure.data.csv/read-csv
                csv
                :separator (or
                             (format :separator)
                             (format :terminator)))
     rows (if (format :terminator) (map drop-last rows-raw) rows-raw)
     preamble-size (count (format :preamble))
     preamble (take preamble-size rows)
     content (drop (+ preamble-size (if (format :column-names-in-file) 1 0)) rows)
     ]
    {:preamble preamble
     :content  (for [row content]
                 (zipmap
                   column-names
                   (parse-csv-row format parsers row)))
     }
    ))

;; Public API

(defn load-format
  "Loads a format from file"
  [format-path]
  (->
    format-path
    slurp
    edn/read-string
    :format))

(defn load-format-resource
  "Loads a format from resource."
  [format-resource-name]
  (load-format (io/resource format-resource-name)))

(defn load-csv
  "Loads and parses a csv according to the given format"
  [format csv-path]
  (->>
    (slurp csv-path :encoding (format :encoding))
    (parse-csv format)))

