console.log("this is js file");
const toggleSidebar = ()=>{
    if($(".sidebar").is(":visible"))
    {
        //to hide the sidebar
console.log("to hide");
        $(".sidebar").css("display","none");
        $(".content").css("margin-left","0%");
    }
    else{
	console.log("to show");
                //to show the sidebar
                $(".sidebar").css("display","block");
                $(".content").css("margin-left","18%");
    }
};

const search=()=>{
    let query = $("#search-input").val();
    if(query=="")
    {
        $(".search-result").hide();
    }
    else{
        console.log(query);
        

        //sending request to the server
        let url=`http://localhost:8080/search/${query}`;
        fetch(url, { headers : { 
            'Content-Type': "application/json;",
            "Accept": "application/json; odata=verbose"

           }})
        .then((response)=>{
            return response.json();
        })
        .then((data)=>{

            //data
            console.log(data);
        });
        $(".search-result").show();
    }
};