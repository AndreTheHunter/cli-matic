(ns cli-matic.platform
  "
  ## Platform-specific functions for ClojureScript and the JVM.
  "
  #?(:clj (:refer-clojure :exclude [read-string]))
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str]
            #?(:cljs [planck.core :as plk])
            #?(:cljs [planck.environ :as plkenv])
            #?(:clj [clojure.edn :refer [read-string]])))

(defn read-env
  "Reads an environment variable.
  If undefined, returns nil."
  [var]
  #?(:clj  (System/getenv var)
     :cljs (let [kw (keyword (str/lower-case var))]
             (get plkenv/env kw nil))))

(defn exit-script
  "Terminates execution with a return value."
  [retval]
  #?(:clj  (System/exit retval)
     :cljs (plk/exit retval)))

#?(:clj
   (defn add-shutdown-hook
     "Add a shutdown hook. If `nil`, simply ignores it.

     The shutdown hook is run in a new thread."
     [fnToCallOnShutdown]
     (when (ifn? fnToCallOnShutdown)
       (.addShutdownHook
         (Runtime/getRuntime)
         (Thread. ^Runnable fnToCallOnShutdown)))))

(defn slurp-file
  "Reads the contents of a file to a string."
  [f]
  #?(:clj  (slurp f)
     :cljs (plk/slurp f)))

#?(:clj
   (do
     ;
     ; Conversions
     ;

     (defn parseInt
       "Converts a string to an integer. "
       [s]
       (Integer/parseInt s))

     (defn parseFloat
       "Converts a string to a float."
       [s]
       (Float/parseFloat s))

     (defn asDate
       "Converts a string in format yyyy-mm-dd to a
       Date object; if conversion
       fails, returns nil."
       [s]
       (try
         (.parse
           (java.text.SimpleDateFormat. "yyyy-MM-dd") s)
         (catch Throwable _
           nil)))

     (defn parseEdn
       "
             Decodes EDN through clojure.edn.
             "
       [edn-in]
       (read-string edn-in)))

   :cljs
   (do
     (defn slurp-file
       "
       Luckily, Planck implements slurp for us.

       No slurping in Node-land.

       See https://github.com/pkpkpk/cljs-node-io

       "
       [f])

     ;
     ; Conversions
     ;

     (defn parseInt
       "Converts a string to an integer. "
       [s]
       (js/parseInt s))

     (defn parseFloat
       "Converts a string to a float."
       [s]
       (js/parseFloat s))

     (defn asDate
       "Converts a string in format yyyy-mm-dd to a
       Date object; if conversion
       fails, returns nil."
       [s]
       (throw (ex-info "Dates not supported in CLJS." {:date s})))

     (defn parseEdn
       "
         This is actually a piece of ClojureScript, though it lives in a different NS.

         See https://cljs.github.io/api/cljs.reader/read-string
       "
       [edn-in]
       (read-string edn-in))))
