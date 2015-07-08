    /** callback for calling the web service (dbpedia lookup)
     * arguments: query: the (partial) keyword for dbpedia
     * callback: inner selectize function  */
var loadFunction = function(query, callback) {
      console.log("query:" + query);
      $.ajax({
        type: "GET",
        dataType: "json",
        url: "http://lookup.dbpedia.org/api/search.asmx/KeywordSearch",
        data: {
          QueryString: query
        },
        beforeSend: function(xhr) {
          xhr.setRequestHeader("Accept", "application/json");
        },
        error: function() {
          callback();
        },
        success: function(res) {
          var json = res.results;
          for (var entry in json) {
             // hack to filter options displayed on dropdown; see searchField below
             json[entry]["query"] = query;
            console.log(json[entry]);
          }
          callback(res.results);
        }
      });
    };

// TODO handle selection from arrow keys
// TODO use score function to avoid partial matching ?

    // DEBUG:
    var onInitializeFunction = function(data) {
      console.log("selectize.js is initialized");
    };

    // selectize options
    var options = {
      onInitialize: onInitializeFunction,
      plugins: ["remove_button" /*, "drag_drop"*/],
      delimiter: ",",
      create: true,
      highlight: false,
      persist: false,
      valueField: "uri",
      labelField: "label",
      searchField: "query",
      load: loadFunction,
      render: {
        option_create: function(data, escape) {
          return "";
        },
        option: function(data, escape) {
          return "<div>" + data.label + "</div>";
        }
      },
    }

    /** tell selectize to do its job on this HTML element */
    var selectizeTarget = $("#input-tags");
    // DEBUG: var tmp = (selectizeTarget.selectize(options)[0]); console.log(tmp);
    var selectize = (selectizeTarget.selectize(options)[0]).selectize;
    selectize.on("load", function(data) {
      console.log("loaded");
      for (var option in data) {
        console.log(data[option]);
        selectize.addOption({
          uri: data[option].uri,
          label: data[option].label
        });
      }
      selectize.refreshOptions(true);
    });

    val completionTag = $("#input-proxy")
    /** wait some delay to send the characters typed */
    var delayFunction = function() {
      console.warn("items : " + this.items);
      this.setTextboxValue( completionTag.val());
      console.log("will query : " + completionTag.val());
      var fn = this.settings.load;
      // TODO do nothing on empty string
      // undocumented selectize callback:
      this.load(function(callback) {
        fn.apply(self, [completionTag.val(), callback]);
      });
      this.refreshOptions(true);
    }.bind(selectize);

    var timer = timer || 0;
    completionTag.keydown(function(e) {
      clearInterval(timer);
    });
    completionTag.keyup(function(e) {
      timer = window.setTimeout(delayFunction, 333);
    });
    // TODO à revoir
    completionTag.on("blur", function() {
      this.focus();
    })
    completionTag.focus();
