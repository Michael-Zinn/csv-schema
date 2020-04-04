(defproject de.michaelzinn/csv-schema "0.1.0-SNAPSHOT"
  :description "Use schemata to read CSV files."
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/core.match "1.0.0"]
                 [org.clojure/data.csv "1.0.0"]]
  :repl-options {:init-ns csv-schema.core})
