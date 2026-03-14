const baseUrl = ""; // אותו דומיין (localhost:8080)

function getCookie(name) {
  const value = `; ${document.cookie}`;
  const parts = value.split(`; ${name}=`);
  if (parts.length === 2) return parts.pop().split(";").shift();
  return null;
}

async function sendPost(path, jsonOrFormData) {
  const csrfToken = getCookie("XSRF-TOKEN");

  const headers = {};
  if (csrfToken) headers["X-XSRF-TOKEN"] = csrfToken; // RAW token

  if (!(jsonOrFormData instanceof FormData)) {
    headers["Content-Type"] = "application/json";
    jsonOrFormData = JSON.stringify(jsonOrFormData);
  }

  return fetch(`${baseUrl}${path}`, {
    method: "POST",
    headers,
    body: jsonOrFormData,
    credentials: "same-origin" // חשוב! שישלח cookies
  });
}