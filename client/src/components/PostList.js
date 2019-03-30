import React from 'react';
import Post from './Post';

class PostList extends React.Component{    
    render(){
        return(
            <div>
               {this.props.hits.map(h => <Post info={h._source} key={h._id}/>)}  
            </div>
        )
    }
}

export default PostList;