import React from 'react';
import { Button, Form, FormGroup, Label, Input, FormText } from 'reactstrap';
import './Search.css';
import Axios from 'axios';

export default class Search extends React.Component {
    constructor(props){
        super(props);
        this.state = { query: '', results: [] };
        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    componentDidMount() {
        //console.log(this.props.location);
    }

    handleChange(e) {
        this.setState({query: e.target.value});
        //console.log(this.state.query);
    }

    handleSubmit(e) {
        console.log(this.state.query);
        Axios.get('http://localhost:8080/search?q=' + this.state.query)
            .then((response) => {
                this.setState({results: response.data.hits})
                console.log(this.state.results)
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
                <Label for="searchBar">/dpt/ archive</Label>
                <Input
                    type="search"
                    name="search"
                    id="searchBar"
                    placeholder="Search for a post"
                    onChange={this.handleChange}
                    />
            </FormGroup>
        </Form>
        </div>
        );
    }
}