import React from 'react';
import Post from './Post';

function PostList(props) {
    console.log(props);
    return(
        <div>
            {props.hits.map(h => <Post info={h._source}/>)}
        </div>
    )
}

export default PostList;