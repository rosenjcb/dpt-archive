import React from 'react';
import { Button, Form, FormGroup, Label, Input, FormText } from 'reactstrap';
import './Search.css';
import Axios from 'axios';
import PostList from './PostList';

export default class Search extends React.Component {
    constructor(props){
        super(props);
        this.state = { query: '', results: [], anyResults: false };
        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    handleChange(e) {
        this.setState({query: e.target.value});
    }

    handleSubmit(e) {
        //http://localhost:8080
        Axios.get('http://localhost:8080/search?q=' + this.state.query)
            .then((response) => {
                this.setState({results: response.data.hits.hits, anyResults: true})
            })
            .catch(function (error){
                console.log(error)
            })
            .then(function(){
                console.log("GET request complete")
            })
        e.preventDefault();
    }

    render(){
        return(
        <div className="search">
            <Form onSubmit={this.handleSubmit}>
                <FormGroup>
                    <Input
                        type="search"
                        name="search"
                        id="searchBar"
                        placeholder="Search for a post"
                        onChange={this.handleChange}
                        />
                </FormGroup>
            </Form>
            { this.state.anyResults
                ? <PostList hits={this.state.results}/>
                : null
            }
        </div>
        );
    }
}