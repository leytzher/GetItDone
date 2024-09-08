(ns ^:figwheel-hooks leytzher.todo
  (:require
   [goog.dom :as gdom]
   [reagent.core :as reagent :refer [atom]]
   [reagent.dom :as rdom]))

;; function to create a todo map. It assigns a random id and default arguments if not provided
(defn create-todo [& {:keys [title done?]
                      :or {title "No Title" done? false}}]
  {:id (random-uuid)
   :title title
   :done? done?})

;; initial todos
(def initial-todos
  [(create-todo :title "Learn Clojure" :done? true)
   (create-todo :title "Build TODO app" :done? false)])

;; state
(defonce todos (reagent/atom initial-todos))

(defn add-todo [todo]
  (swap! todos conj todo))

(defn remove-todo [id]
  (swap! todos (fn [current-todos]
                 (remove #(= (:id %) id) current-todos)) ))
;;; components

(defn card-component [{:keys [id title ]}]
  (let [done? (reagent/atom false)]
    (fn []
      [:div.todo-card
       [:input.todo-checkbox {:type "checkbox"
                :id id
                :checked done?
                :on-change #(swap! done? not)}]
       [:label.todo-text {:for id :style {:text-decoration (when @done? "line-through")}} title]
       [:button.remove-button {:on-click #(remove-todo id)} "Remove"]])))


(defn todo-form []
  (let [new-todo (reagent/atom "")]
    (fn []
      [:div.form
       [:input.input {:type "text"
                :value @new-todo
                :on-change #(reset! new-todo (.. % -target -value))
                :placeholder "Enter a new todo"}]
       [:button.button {:on-click #(when (seq @new-todo)
                              (add-todo (create-todo :title @new-todo))
                              (reset! new-todo ""))}
        "Add Todo"]])))


(defn todo-list [todos]
  [:div
   (for [todo @todos]
     ^{:key (:id todo)}
     [card-component todo ])])

(defn todo []
  [:div.app
   [:h3.title "Get It Done!"]
   [todo-form]
   [todo-list todos]])

(defn mount-app-element []
  (rdom/render [todo] (gdom/getElement "app")))

(mount-app-element)

(defn ^:after-load on-reload []
  (mount-app-element))
