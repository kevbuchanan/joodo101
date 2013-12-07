(ns clojure101.init
  (:require [joodo.env :as env]))

(defn init []
  (env/load-configurations))
