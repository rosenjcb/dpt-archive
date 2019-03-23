import React from 'react';
import './Post.css';

class Post extends React.Component {
    constructor(props){
        super(props);
        console.log(this.props)
        this.state = { processed_comment: this.props.info.comment};
    }

    componentDidMount() {
        //this.setState({processed_comment: this.props.comment.replace(/(?:\r\n|\r|\n)/g, '<br />')});
        //console.log(this.state.processed_comment)
    }

    render() {
        return(
            <div>
                <div className="sideArrows">>></div>
                <div className="post">
                    <div className="postInfo">
                        <input type = "checkbox"/>
                        <span className="name">{this.props.info.name}</span>
                        <span className="dateTime">{this.props.info.fourchan_date}</span>
                        <span className="postNum"><a href={"#p" + this.props.info.num} title="Link to this post">No.{this.props.info.num}</a></span>
                        <a href="#" class="postMenuBtn" title="Post menu">▶</a>
                    </div>
                    <span>{this.state.processed_comment}</span>
                </div>
            </div>
        );
    }
}

export default Post;