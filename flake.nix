# flake.nix
{
  description = "My development shell";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixos-unstable";
  };

  outputs = { self, nixpkgs }:
  let
    system = "aarch64-darwin";
    pkgs = import nixpkgs {
      inherit system;
      environment.systemPackages = with pkgs; [
        git
        nodejs
        neovim
      ];
    };
  in {
    devShells.${system}.default = pkgs.mkShell {
      buildInputs = with pkgs; [
        git
        jdk17
        nodejs
        neovim
      ];
    };
  };
}
