require 'json'
package_json = JSON.parse(File.read('package.json'))

Pod::Spec.new do |s|
  s.name           = "react-native-neura"
  s.version        = package_json["version"]
  s.summary        = package_json["description"]
  s.homepage       = package_json["homepage"]
  s.license        = package_json["license"]
  s.author         = package_json["author"]
  s.platform       = :ios, "9.0"
  s.source         = { :git => "#{package_json["repository"]["url"]}.git", :tag => "#{s.version}" }
  s.source_files   = 'ios/*.{h,m}'
  
  s.dependency 'React',  '>= 0.13.0', '< 1.0.0'

  # The Native Neura-iOS-SDK from cocoapods.
  s.dependency 'NeuraSDKFramework'
end