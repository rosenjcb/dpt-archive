import React from 'react';
import './Post.css';

class Post extends React.Component {
    constructor(props){
        super(props);
        this.state = { processed_comment: this.props.info.comment};
    }

    processComment(raw_comment) {
        var result = [];
        let lines = raw_comment.split(/(?:\r\n|\r|\n)/g);
        for(var line of lines) {
            if(line[0] === ">" && line[1] !== ">") {
                result.push(<div className="customblockquote">{line}</div>);
                result.push(<br />);
            } else if(line[0] === ">" && line[1] === ">") {
                result.push(<div className="customquotelink">{line}</div>);
                result.push(<br />);
            } 
            else {
                result.push(line);
                result.push(<br />);  
            }
        }
        return result;
    }

    /**
     * 'Warning: Each child in a list should have a unique "key" prop.' <- Error at Runtime
     * This was fixed when I added a key to the top div, but this stopped working after replacing state with a const in render.
     * I wonder why.  
     */
    render() {
        const processed_comment = this.processComment(this.props.info.comment);
        return(
            <div key={this.props.info._id}>
                <div className="sideArrows">>></div>
                <div className="post">
                    <div className="postInfo">
                        <input type = "checkbox"/>
                        <span className="name">{this.props.info.name}</span>
                        <span className="dateTime">{this.props.info.fourchan_date}</span>
                        <span className="postNum"><a href={"#p" + this.props.info.num} title="Link to this post">No.{this.props.info.num}</a></span>
                        <a href="#" className="postMenuBtn" title="Post menu">▶</a>
                    </div>
                    {
                        this.props.info.media
                        ? <div className="filethumb"><img src={this.props.info.media.thumb_link} /></div>
                        : null
                    }
                    <blockquote className="postMessage">{processed_comment}</blockquote>
                </div>
            </div>
        );
    }
}

export default Post;