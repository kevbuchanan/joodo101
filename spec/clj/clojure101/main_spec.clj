(ns clojure101.main-spec
  (:require [speclj.core :refer :all]
            [joodo.spec-helpers.controller :refer :all]
            [clojure101.main :refer :all]))

(describe "clojure101"

  (with-mock-rendering)
  (with-routes app-handler)

  (it "handles root"
    (let [result (do-get "/")]
      (should= 200 (:status result))
      (should= "index" @rendered-template)))


  (it "checks a low guess"
    (let [result (do-post "/guess" :params {:guess "1"})]
      (should= 303 (:status result))
      (should= "low" (:guess (:cookies result)))
      (should= 6 (:left (:cookies result)))))

  (it "checks a high guess"
      (let [result (do-post "/guess" :params {:guess "35"})]
        (should= 303 (:status result))
        (should= "high" (:guess (:cookies result)))
        (should= 6 (:left (:cookies result)))))

  (it "checks a correct guess"
      (let [result (do-post "/guess" :params {:guess "12"})]
        (should= 303 (:status result))
        (should= "correct" (:guess (:cookies result)))
        (should= 7 (:left (:cookies result)))))

  (it "restarts when out of guesses"
      (let [result (do-get "/guess" :cookies {:guess {:value "1"} :left {:value "0"}})]
        (should= 302 (:status result))
        (should= "/" ((:headers result) "Location"))))
  )
(run-specs)
