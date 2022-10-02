# Welcome to Kompendium contributing guide <!-- omit in toc -->

Thank you for investing your time in contributing to our project! Any contribution you make will help build the best OpenAPI generator for Ktor :sparkles:.

Read our [Code of Conduct](./CODE_OF_CONDUCT.md) to keep our community approachable and respectable.

In this guide you will get an overview of the contribution workflow from opening an issue, creating a PR, reviewing, and merging the PR.

Use the table of contents icon in the top left corner of this document to get to a specific section of this guide quickly.

## New contributor guide

To get an overview of the project, read the [README](README.md). Here are some resources to help you get started with open source contributions:

- [Finding ways to contribute to open source on GitHub](https://docs.github.com/en/get-started/exploring-projects-on-github/finding-ways-to-contribute-to-open-source-on-github)
- [Set up Git](https://docs.github.com/en/get-started/quickstart/set-up-git)
- [GitHub flow](https://docs.github.com/en/get-started/quickstart/github-flow)
- [Collaborating with pull requests](https://docs.github.com/en/github/collaborating-with-pull-requests)


## Getting started

The best way to get started with Kompendium is to read the [docs](https://bkbn.gitbook.io/kompendium). 

If live examples are more your thing, inside the `kompendium-playground` module, you will find a collection of starter 
examples ranging from simple CRUD and authentication examples, to more advanced topics like polymorphic serialization 
and custom type overrides.  

### Issues

#### Create a new issue

If you've discovered an issue with Kompendium, or have an idea for a new feature, [search if an issue already exists](https://docs.github.com/en/github/searching-for-information-on-github/searching-on-github/searching-issues-and-pull-requests#search-by-the-title-body-or-comments). If a related issue doesn't exist, you can open a new issue using a relevant [issue form](https://github.com/bkbnio/kompendium/issues/new/choose).

#### Solve an issue

Scan through our [existing issues](https://github.com/bkbnio/kompendium/issues) to find one that interests you.  As a general rule, issue assignment is informal and not required.  If you find an issue to work on, you are welcome to open a PR with a fix.

### Make Changes

#### Make changes locally

1. Fork the repository.
- Using GitHub Desktop:
  - [Getting started with GitHub Desktop](https://docs.github.com/en/desktop/installing-and-configuring-github-desktop/getting-started-with-github-desktop) will guide you through setting up Desktop.
  - Once Desktop is set up, you can use it to [fork the repo](https://docs.github.com/en/desktop/contributing-and-collaborating-using-github-desktop/cloning-and-forking-repositories-from-github-desktop)!

- Using the command line:
  - [Fork the repo](https://docs.github.com/en/github/getting-started-with-github/fork-a-repo#fork-an-example-repository) so that you can make your changes without affecting the original project until you're ready to merge them.

- GitHub Codespaces:
  - [Fork, edit, and preview](https://docs.github.com/en/free-pro-team@latest/github/developing-online-with-codespaces/creating-a-codespace) using [GitHub Codespaces](https://github.com/features/codespaces) without having to install and run the project locally.

2. Install Java.  Any version of Java 11+ should be fine. 

3. Create a working branch and start with your changes!

### Commit your update

Commit the changes once you are happy with them.  Git hooks are automatically installed via Gradle that will run the linter
on every commit, and run the tests prior to pushing your code.  This helps speed up the review cycle immensely.  If you would like
to push a draft PR that skips the hooks, you can simply add the `--no-verify` flag, ie `git commit --no-verify -m "commit message"`.

### Pull Request

When you're finished with the changes, you are ready to create a pull request! There is no hard and fast rules here, but 
do your best to fill out the pull request template with all the requisite information! 

### Your PR is merged!

Congratulations :tada::tada: The Kompendium team thanks you :sparkles:.

Once your PR is merged, it will go out in the next release cycle.  We often keep these cycles super tight, it is not uncommon for each PR to get its own release. 
