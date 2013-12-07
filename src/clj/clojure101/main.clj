(ns clojure101.main
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [joodo.middleware.asset-fingerprint :refer [wrap-asset-fingerprint]]
            [joodo.middleware.favicon :refer [wrap-favicon-bouncer]]
            [joodo.middleware.keyword-cookies :refer [wrap-keyword-cookies]]
            [joodo.middleware.request :refer [*request* wrap-bind-request]]
            [joodo.middleware.util :refer [wrap-development-maybe]]
            [joodo.middleware.view-context :refer [wrap-view-context]]
            [joodo.views :refer [render-template render-html]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [ring.middleware.flash :refer [wrap-flash]]
            [ring.middleware.head :refer [wrap-head]]
            [ring.middleware.keyword-params :refer [wrap-keyword-params]]
            [ring.middleware.multipart-params :refer [wrap-multipart-params]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.refresh :refer [wrap-refresh]]
            [ring.middleware.session :refer [wrap-session]]
            [ring.util.response :refer [redirect-after-post redirect]]
            [shoreleave.middleware.rpc :refer [wrap-rpc]]))

(defn check-guess [params]
  (let [guess (Integer/parseInt (params :guess))
        left (dec (Integer/parseInt (or (:value (:left (:cookies *request*))) "7")))
        correct 12
        set-cookie (partial assoc (redirect-after-post "/guess") :cookies)]
    (cond
      (< guess correct) (set-cookie {:guess "low" :left left})
      (> guess correct) (set-cookie {:guess "high" :left left})
      :else (set-cookie {:guess "correct" :left 7}))))

(defn get-another-guess [params]
  (let [guess (:value (:guess (:cookies *request*)))
        left (Integer/parseInt (:value (:left (:cookies *request*))))
        response (partial render-template "index" :message)]
    (cond
      (= guess "correct") (response "Correct")
      (= left 0) (assoc (redirect "/") :cookies {:left 7})
      :else (response (str "Too " guess ". " left " guesses left")))))

(defroutes app-routes
  (GET "/" [] (render-template "index" :message "Guess a number between 1 and 100"))
  (GET "/guess" {params :params} (get-another-guess params))
  (POST "/guess" {params :params} (check-guess params))
  (route/not-found (render-template "not_found" :template-root "clojure101" :ns `clojure101.view-helpers)))

(def app-handler
  (->
    app-routes
    (wrap-view-context :template-root "clojure101" :ns `clojure101.view-helpers)
    wrap-rpc))

(def app
  (-> app-handler
    wrap-development-maybe
    wrap-refresh
    wrap-bind-request
    wrap-keyword-params
    wrap-params
    wrap-multipart-params
    wrap-flash
    wrap-keyword-cookies
    wrap-session
    wrap-favicon-bouncer
    (wrap-resource "public")
    wrap-asset-fingerprint
    wrap-file-info
    wrap-head))
